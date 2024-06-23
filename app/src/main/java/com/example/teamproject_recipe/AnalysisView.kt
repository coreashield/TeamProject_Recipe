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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(navController: NavHostController, imageUri: Uri?) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "재료를 파악했어요.",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
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

            imageUri?.let {
                ImageWithBoundingBox(
                    imageUri = it,
                    boundingBoxes = listOf(
                        BoundingBox(50f, 50f, 150f, 150f),
                        BoundingBox(200f, 200f, 300f, 300f)
                    )
                )
            }

//            Text(
//                text = "재료 이름",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold
//            )
            IngredientsInfo()
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("recipe") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("레시피를 추천해줘! 🍔")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth(),

                ) {
                Text("뒤로 가기")
            }
        }
    }
}


@Composable
fun IngredientsInfo() {
    LazyColumn(
        modifier = Modifier
            .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
            .padding(16.dp)
            .height(80.dp)
    ) {
        items(listOf("Calories", "Protein", "Carbohydrates", "Sugars", "Sodium", "Fat")) { item ->
            IngredientsItem(item)
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
    val bottom: Float
)

// 이미지와 경계 상자를 표시하는 함수
@Composable
fun ImageWithBoundingBox(imageUri: Uri, boundingBoxes: List<BoundingBox>) {
    Box(modifier = Modifier.padding(16.dp)) {
        Image(
            painter = rememberAsyncImagePainter(imageUri), // 전달된 이미지 URI 표시
            contentDescription = null,
            modifier = Modifier
                .size(400.dp),
//                .padding(16.dp),
            contentScale = ContentScale.Crop,
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            boundingBoxes.forEach { box ->
                drawRect(
                    color = Color.Red, // 경계 상자 색상
                    topLeft = androidx.compose.ui.geometry.Offset(box.left, box.top),
                    size = androidx.compose.ui.geometry.Size(box.right - box.left, box.bottom - box.top),
                    style = Stroke(width = 2.dp.toPx()) // 경계 상자 스타일
                )
            }
        }
    }
}