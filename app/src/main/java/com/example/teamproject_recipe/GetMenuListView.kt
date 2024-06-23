package com.example.teamproject_recipe

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuCard(
    recipe: Recipe,
    isFavorite: Boolean,
    onFavoriteClick: (Recipe) -> Unit
) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
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
        Text(text = recipe.description, fontSize = 14.sp, color = Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuListView(favorites: List<Recipe>, onFavoritesChanged: (List<Recipe>) -> Unit) {
    val recipes = listOf(
        Recipe("올리브 소시지 솥밥", "팜조합한 소시지와 올리브의 감칠맛 가득한", "image_url"),
//        Recipe("원 팟 파스타", "냄비 하나로 완성하는 초간단", "image_url"),
//        Recipe("그릴드 브리치즈", "고소한 브리치즈와 새콤한 토마토의 만남", "image_url"),
//        Recipe("그릴드 피치 샐러드", "그릴자국은 널 복숭아가 멋진", "image_url"),
//        Recipe("과카몰리 부리또콘", "양쪽맛은 팡팡콘", "image_url"),
//        Recipe("아스파라거스 부라타 샐러드", "싱그럽고 산뜻한", "image_url"),
//        Recipe("베지케이크", "채소를 더 맛있게", "image_url"),
//        Recipe("그린주스", "건강한 삶을 위한", "image_url")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("추천 레시피")
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
                        }
                    )
                }
            }
        }
    }
}


data class Recipe(
    val title: String,
    val description: String,
    val imageUrl: String
)

