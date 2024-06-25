package com.example.teamproject_recipe

import allIngredients
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    navController: NavHostController,
    response: String?,
    onIngredientsChanged: (String) -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var outputImageUri by remember { mutableStateOf<Uri?>(null) }
    var ingredientsList by remember { mutableStateOf(listOf<String>()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(response) {
        response?.let {
            val outputImage = it.substringAfter("output_image\": \"").substringBefore("\"")
            val imageUrl = RetrofitClient.BASE_URL + "download/$outputImage"
            outputImageUri = Uri.parse(imageUrl)

            val ingredients = it.substringAfter("ingredients\": \"").substringBefore("\",").replace(" ", "")
            onIngredientsChanged(ingredients)
            ingredientsList = ingredients.split(",").filter { ingredient -> ingredient.isNotBlank() }
            isLoading = false
        } ?: run {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("재료를 파악했어요.", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isLoading) {
                    LoadingIndicator()
                } else {
                    ImageWithBoundingBox(imageUri = outputImageUri)
                    Spacer(modifier = Modifier.height(8.dp))
                    IngredientsInfo(ingredientsList) { newIngredient ->
                        ingredientsList = ingredientsList + newIngredient
                        onIngredientsChanged(ingredientsList.joinToString(","))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                uploadIngredientsToFlask(context, ingredientsList.joinToString(",")) { response ->
                                    navController.navigate("recipe")
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp, end = 32.dp)
                    ) {
                        Text("레시피를 추천해줘! 🍔")
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.TopStart
    ) {
        CircularProgressIndicator()
    }
}

// 재료 정보를 표시하는 함수
@Composable
fun IngredientsInfo(ingredients: List<String>, onNewIngredientAdded: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                .padding(8.dp)
                .width(80.dp)
                .height(80.dp)
        ) {
            items(ingredients.filter { it.isNotBlank() }) { ingredient ->
                IngredientsItem(ingredient)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { expanded = true }) {
                Text("재료 추가")
            }
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                allIngredients.forEach { ingredient ->
                    DropdownMenuItem(
                        text = { Text(ingredient) },
                        onClick = {
                            onNewIngredientAdded(ingredient)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// 재료 항목을 표시하는 함수
@Composable
fun IngredientsItem(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = label)
    }
}

// 이미지와 경계 상자를 표시하는 함수
// 이미지와 경계 상자를 표시하는 함수
@Composable
fun ImageWithBoundingBox(imageUri: Uri?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                Text(
                    text = "이미지를 불러오는 중...",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }
    }
}
