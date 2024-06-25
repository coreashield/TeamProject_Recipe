import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.teamproject_recipe.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeInfoView(navController: NavController, recipeId: Int) {
    var recipeDetails by remember { mutableStateOf<RecipeDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isEmptyResponse by remember { mutableStateOf(false) }

    // API 호출
    LaunchedEffect(recipeId) {
        Log.d("RecipeInfoView", "Calling API with recipeId: $recipeId")
        RetrofitClient.instance.getRecipeDetails(recipeId.toString()).enqueue(object : Callback<List<RecipeDetails>> {
            override fun onResponse(call: Call<List<RecipeDetails>>, response: Response<List<RecipeDetails>>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    if (responseBody.isNotEmpty()) {
                        recipeDetails = responseBody[0]
                        Log.d("RecipeInfoView", "API Response: ${responseBody[0]}")
                    } else {
                        isEmptyResponse = true
                        Log.d("RecipeInfoView", "Empty response")
                    }
                } else {
                    Log.d("RecipeInfoView", "Response not successful: ${response.code()}")
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<RecipeDetails>>, t: Throwable) {
                Log.e("RecipeInfoView", "API call failed", t)
                isLoading = false
                isEmptyResponse = true
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "상세 레시피") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (isEmptyResponse) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "레시피를 불러올 수 없습니다.", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                recipeDetails?.let { recipe ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        item {
                            Image(
                                painter = rememberAsyncImagePainter(model = recipe.image),
                                contentDescription = null,
                                contentScale = ContentScale.Inside,
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = recipe.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Ingredients",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Column {
                                recipe.extendedIngredients.forEach { ingredient ->
                                    Text(text = ingredient.original)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "조리법",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            recipe.analyzedInstructions.forEach { instruction ->
                                instruction.steps.forEach { step ->
                                    Text(text = "${step.number}. ${step.step}")
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}