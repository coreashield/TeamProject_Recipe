package com.example.teamproject_recipe

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.io.File

interface FlaskApiService {
    @Multipart
    @POST("/upload_and_find")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ResponseBody>

    @GET("/findfoodlist")
    fun getRecipes(@Query("ingredients") ingredients: String): Call<List<Recipe>>
}

object RetrofitClient {
    private const val BASE_URL = "http://192.168.45.158:5000/"

    val instance: FlaskApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlaskApiService::class.java)
    }
}

fun uploadImageToFlask(context: Context, imageUri: Uri?, onResult: (String?) -> Unit) {
    val file = imageUri?.let { getRealPathFromURI(context, it) }?.let { File(it) }
    val requestBody = file?.let { RequestBody.create("image/*".toMediaTypeOrNull(), it) }
    val body = requestBody?.let { MultipartBody.Part.createFormData("file", file?.name, it) }

    if (body != null) {
        RetrofitClient.instance.uploadImage(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val responseString = responseBody.string()
                        onResult(responseString)
                        showToast(context, "Upload Success: $responseString")
                        Log.d("Upload", "Success: $responseString")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    showToast(context, "Upload Failed: $errorBody")
                    Log.d("Upload", "Failed: $errorBody")
                    onResult(errorBody)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onResult(null)
                showToast(context, "Upload Error: ${t.message}")
                Log.d("Upload", "Error: ${t.message}")
            }
        })
    } else {
        showToast(context, "File is null")
        onResult(null)
    }
}
fun getRealPathFromURI(context: Context, uri: Uri): String? {
    return when {
        DocumentsContract.isDocumentUri(context, uri) -> {
            handleDocumentUri(context, uri)
        }
        "content".equals(uri.scheme, ignoreCase = true) -> {
            getDataColumn(context, uri, null, null)
        }
        "file".equals(uri.scheme, ignoreCase = true) -> {
            uri.path
        }
        else -> null
    }
}

private fun handleDocumentUri(context: Context, uri: Uri): String? {
    val documentId = DocumentsContract.getDocumentId(uri)
    return when (uri.authority) {
        "com.android.providers.media.documents" -> {
            val id = documentId.split(":")[1]
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selection = "_id=?"
            val selectionArgs = arrayOf(id)
            getDataColumn(context, contentUri, selection, selectionArgs)
        }
        "com.android.providers.downloads.documents" -> {
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), documentId.toLong()
            )
            getDataColumn(context, contentUri, null, null)
        }
        "com.android.externalstorage.documents" -> {
            val split = documentId.split(":")
            val type = split[0]
            if (type.equals("primary", true)) {
                "${context.getExternalFilesDir(null)?.path}/${split[1]}"
            } else null
        }
        else -> null
    }
}

fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
    val projection = arrayOf("_data")
    context.contentResolver.query(uri, projection, selection, selectionArgs, null).use { cursor ->
        return if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow("_data")
            cursor.getString(index)
        } else null
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun fetchRecipes(context: Context, ingredients: String, onResult: (List<Recipe>?) -> Unit) {
    RetrofitClient.instance.getRecipes(ingredients).enqueue(object : Callback<List<Recipe>> {
        override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
            if (response.isSuccessful) {
                onResult(response.body())
            } else {
                showToast(context, "Failed to fetch recipes: ${response.errorBody()?.string()}")
                onResult(null)
            }
        }

        override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
            showToast(context, "Error: ${t.message}")
            onResult(null)
        }
    })
}