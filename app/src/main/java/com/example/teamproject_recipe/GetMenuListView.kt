package com.example.teamproject_recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import coil.compose.rememberAsyncImagePainter
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MenuCard(
    recipe: Recipe,
    isFavorite: Boolean,
    onFavoriteClick: (Recipe) -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = recipe.image),
                contentDescription = recipe.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            IconButton(
                onClick = { onFavoriteClick(recipe) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = recipe.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuListView(navController: NavController, favorites: List<Recipe>, onFavoritesChanged: (List<Recipe>) -> Unit) {
    val context = LocalContext.current
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }

    // Fetch recipes when the Composable is first composed
    LaunchedEffect(Unit) {
        fetchRecipes(context, "rice,milk,onion") { fetchedRecipes ->
            if (fetchedRecipes != null) {
                recipes = fetchedRecipes
            } else {
                showToast(context, "Failed to load recipes")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("추천 레시피")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                items(recipes) { recipe ->
                    val isFavorite = recipe in favorites
                    MenuCard(
                        recipe = recipe,
                        isFavorite = isFavorite,
                        onFavoriteClick = { selectedRecipe ->
                            onFavoritesChanged(
                                if (isFavorite) {
                                    favorites - selectedRecipe
                                } else {
                                    favorites + selectedRecipe
                                }
                            )
                        },
                        onClick = {
                            val encodedTitle = URLEncoder.encode(recipe.title, StandardCharsets.UTF_8.toString())
                            val encodedImage = URLEncoder.encode(recipe.image, StandardCharsets.UTF_8.toString())
                            navController.navigate("recipeInfo/$encodedTitle/$encodedImage")
//                            navController.navigate("recipeInfo/${recipe.title}/${recipe.image}")
                        }
                    )
                }
            }
        }
    }
}

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val likes: Int,
    val missedIngredientCount: Int,
    val missedIngredients: List<Ingredient>,
    val usedIngredientCount: Int,
    val usedIngredients: List<Ingredient>
)

data class Ingredient(
    val id: Int,
    val aisle: String,
    val amount: Double,
    val image: String,
    val name: String,
    val original: String,
    val unit: String
)