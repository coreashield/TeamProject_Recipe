import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.teamproject_recipe.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "레시피를 부탁해") },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = "https://via.placeholder.com/150"), // Replace with your image URL or resource
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "메뉴 이름",
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
                MenuItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_1", navController)
                MenuItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_2", navController)
                MenuItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_3", navController)
                MenuItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_4", navController)
                MenuItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_5", navController)
                MenuItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_6", navController)
                MenuItem(imageRes = R.drawable.ic_launcher_foreground, name = "Name_7", navController)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "조리법",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun MenuItem(imageRes: Int, name: String, navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { navController.navigate("recipeInfoView") }
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