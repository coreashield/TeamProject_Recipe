package com.example.teamproject_recipe

import RecipeInfoView
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
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
            PackageManager.PERMISSION_GRANTED
        ) {
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
    val items = listOf("Home", "Favorite", "Profile")
    val icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
    var favorites by rememberSaveable { mutableStateOf(listOf<Recipe>()) }

    Scaffold(
        snackbarHost = { SnackbarHost(scaffoldState) },
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "레시피를 부탁해",
                            fontWeight = FontWeight.Bold,
                            color = White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Red),
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(indicatorColor = White),
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = false, // 선택 여부 로직 추가 필요
                        onClick = {
                            when (item) {
                                "Home" -> navController.navigate("home")
                                "Favorite" -> navController.navigate("favorite")
                                "Profile" -> navController.navigate("profile/이름") // 예시 userId
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHostContainer(
            navController = navController,
            paddingValues = paddingValues,
            favorites = favorites,
            onFavoritesChanged = { favorites = it })
    }
}


@Composable
fun NavHostContainer(
    navController: NavHostController,
    paddingValues: PaddingValues,
    favorites: List<Recipe>,
    onFavoritesChanged: (List<Recipe>) -> Unit
) {
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    NavHost(
        navController = navController,
        startDestination = "home",
        Modifier.padding(paddingValues)
    ) {
        composable("home") {
            HomeScreen(navController, imageUri, onImageUriChanged = { imageUri = it })
        }

        composable("favorite") {
            FavoriteScreen(favorites = favorites, onFavoriteClick = { recipe ->
                val updatedFavorites = if (favorites.contains(recipe)) {
                    favorites - recipe
                } else {
                    favorites + recipe
                }
                onFavoritesChanged(updatedFavorites)
            })
        }

        composable("profile/{userId}") { backStackEntry ->
            ProfileScreen(userId = backStackEntry.arguments?.getString("userId"))
        }

        composable("camera") {
            CameraPreviewScreen(
                onBack = { navController.popBackStack() },
                onImageCaptured = { uri ->
                    // 이미지 캡처 후 처리 로직 추가
                }
            )
        }

        composable("analysis/{imageUri}") { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("imageUri")
            val decodedUri = encodedUri?.let { Uri.parse(Uri.decode(it)) }
            AnalysisScreen(navController, imageUri)
        }

        composable("recipe") {
            MenuListView(favorites, onFavoritesChanged)
        }

        composable("recipeInfo") {
            RecipeInfoView(navController)
        }
    }
}


@Composable
fun HomeScreen(
    navController: NavController,
    initialImageUri: Uri?,
    onImageUriChanged: (Uri?) -> Unit
) {
    var isCameraPreviewVisible by remember { mutableStateOf(false) }
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            onImageUriChanged(it)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        if (isCameraPreviewVisible) {
            CameraPreviewScreen(
                onBack = { isCameraPreviewVisible = false },
                onImageCaptured = { uri ->
                    onImageUriChanged(uri)
                    isCameraPreviewVisible = false
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Image(
                    painter = rememberAsyncImagePainter(initialImageUri),
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .size(400.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { isCameraPreviewVisible = true }) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Camera Icon",
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("재료 사진 찍기")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.AccountBox, // 사용할 아이콘 설정
                        contentDescription = "Select GalleryImage",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이에 간격 추가
                    Text("갤러리")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val encodedUri = Uri.encode(initialImageUri.toString())
                        navController.navigate("analysis/$encodedUri")
                    },
                    enabled = initialImageUri != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Select GalleryImage",
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("재료 파악하기")
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(userId: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$userId profile Screen")
    }
}
