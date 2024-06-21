package com.example.teamproject_recipe

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    setContent {
                        MainScreen()
                    }
                } else {
                    Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED) {
            setContent {
                MainScreen()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val scaffoldState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val items = listOf("Home", "Favorite", "Profile")
    val icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)

    Scaffold(
        snackbarHost = { SnackbarHost(scaffoldState) },
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("레시피를 부탁해")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = White),
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = false, // 선택 여부 로직 추가 필요
                        onClick = {
                            when (item) {
                                "Home" -> navController.navigate("home")
                                "Favorite" -> navController.navigate("favorite")
                                "Profile" -> navController.navigate("profile/123") // 예시 userId
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHostContainer(navController = navController, paddingValues = paddingValues)
    }
}

@Composable
fun NavHostContainer(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(navController = navController, startDestination = "home", Modifier.padding(paddingValues)) {
        composable("home") { HomeScreen() }
        composable("favorite") { FavoriteScreen() }
        composable("profile/{userId}") { backStackEntry ->
            ProfileScreen(userId = backStackEntry.arguments?.getString("userId"))
        }
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isCameraPreviewVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isCameraPreviewVisible) {
            CameraPreviewScreen(
                onBack = { isCameraPreviewVisible = false },
                onImageCaptured = { uri ->
                    imageUri = uri
                    isCameraPreviewVisible = false
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Captured Image",
                        modifier = Modifier
                            .size(400.dp)
                            .padding(16.dp),
                        contentScale = ContentScale.Crop
                    )
                }
//                } else {
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_launcher_background), // 기본 이미지 리소스
//                        contentDescription = "Ingredient Image",
//                        modifier = Modifier
//                            .size(200.dp)
//                            .padding(16.dp)
//                    )
//                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { isCameraPreviewVisible = true }) {
                        Text("사진찍기")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { pickMediaLauncher.launch(PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Text("갤러리")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* 레시피 검색 로직 */ }) {
                    Text("레시피 검색")
                }
            }
        }
    }
}

@Composable
fun FavoriteScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Favorite Screen")
    }
}

@Composable
fun ProfileScreen(userId: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$userId profile Screen")
    }
}
