package com.example.teamproject_recipe

import android.net.Uri
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(navController: NavHostController, imageUri: Uri?, response: String?) {
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Simulate a delay for the loading indicator (replace this with actual network call)
        scope.launch {
            delay(2000) // Simulate loading time
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "재료를 파악했어요.",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isLoading) {
                LoadingIndicator()
            } else {
                imageUri?.let {
                    ImageWithBoundingBox(
                        imageUri = it,
                        boundingBoxes = emptyList()  // 문자열 응답에는 경계 상자가 없으므로 빈 리스트를 전달
                    )
                }
                response?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(response)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("recipe") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("레시피를 추천해줘! 🍔")
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
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun VerticalResponseText(response: String) {
    LazyColumn(
        modifier = Modifier
            .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
            .padding(16.dp)
            .height(200.dp)
    ) {
        items(response.toList()) { character ->
            Text(
                text = character.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun IngredientsInfo(ingredients: List<String>) {
    LazyColumn(
        modifier = Modifier
            .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
            .padding(16.dp)
            .height(200.dp)
    ) {
        items(ingredients) { ingredient ->
            IngredientsItem(ingredient)
        }
    }
}

@Composable
fun IngredientsItem(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = label)
    }
}


// 경계 상자 좌표를 저장하기 위한 데이터 클래스 정의
data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
)

// 이미지와 경계 상자를 표시하는 함수
@Composable
fun ImageWithBoundingBox(imageUri: Uri, boundingBoxes: List<BoundingBox>) {
    Box(modifier = Modifier.padding(16.dp)) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier.size(320.dp),
            contentScale = ContentScale.Crop,
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            boundingBoxes.forEach { box ->
                drawRect(
                    color = Color.Red,
                    topLeft = androidx.compose.ui.geometry.Offset(box.left, box.top),
                    size = androidx.compose.ui.geometry.Size(
                        box.right - box.left,
                        box.bottom - box.top
                    ),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}