import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.teamproject_recipe.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeInfoView(navController: NavController, recipeTitle: String?, recipeImage: String?) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                Image(
                    painter = rememberAsyncImagePainter(model = recipeImage ?: "https://via.placeholder.com/150"),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recipeTitle ?: "메뉴 이름",
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IngredientItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_1")
                    IngredientItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_2")
                    IngredientItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_3")
                    IngredientItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_4")
                    IngredientItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_5")
                    IngredientItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_6")
                    IngredientItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_7")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "조리법",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 추가 레시피 항목들
            items(10) { index ->
                Text(
                    text = "조리법 항목 ${index + 1}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
@Composable
fun IngredientItem(imageRes: Int, name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val description: String
)