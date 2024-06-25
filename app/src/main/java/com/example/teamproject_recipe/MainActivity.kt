package com.example.teamproject_recipe

import ProfileScreen
import RecipeInfoView
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    var selectedItem by remember { mutableIntStateOf(0) }
    var favorites by rememberSaveable { mutableStateOf(listOf<Recipe>()) }
    var isLoading by remember { mutableStateOf(false) }
    var ingredients by remember { mutableStateOf("") }

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
            NavigationBar(containerColor = Color(0xFFFF0000),
                modifier = Modifier.height(56.dp)) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = White,
                            selectedIconColor = White
                        ),
                        icon = { Icon(icons[index], contentDescription = item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            NavHostContainer(
                navController = navController,
                paddingValues = paddingValues,
                favorites = favorites,
                onFavoritesChanged = { favorites = it },
                onLoadingChanged = { isLoading = it },
                ingredients = ingredients,
                onIngredientsChanged = { ingredients = it }
            )
        }
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    paddingValues: PaddingValues,
    favorites: List<Recipe>,
    onFavoritesChanged: (List<Recipe>) -> Unit,
    onLoadingChanged: (Boolean) -> Unit,
    ingredients: String,
    onIngredientsChanged: (String) -> Unit
) {
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var uploadResponse by rememberSaveable { mutableStateOf<String?>(null) }

    NavHost(
        navController = navController,
        startDestination = "home",
        Modifier.padding(paddingValues)
    ) {
        composable("home") {
            HomeScreen(
                navController,
                imageUri,
                onImageUriChanged = { imageUri = it },
                onUploadResponse = { uploadResponse = it },
                onLoadingChanged = onLoadingChanged
            )
        }

        composable("favorite") {
            FavoriteScreen(favorites = favorites, onFavoriteClick = { recipe ->
                val updatedFavorites = if (favorites.contains(recipe)) {
                    favorites - recipe
                } else {
                    favorites + recipe
                }
                onFavoritesChanged(updatedFavorites)
            }, navController = navController)
        }

        composable("profile/{userId}") { backStackEntry ->
            backStackEntry.arguments?.getString("userId")?.let { ProfileScreen(userId = it) }
        }

        composable("camera") {
            CameraPreviewScreen(
                onBack = { navController.popBackStack() },
                onImageCaptured = { uri ->
                    // 이미지 캡처 후 처리 로직 추가
                }
            )
        }

        composable("analysis/{imageUri}") {
            AnalysisScreen(navController, uploadResponse, onIngredientsChanged)
        }

        composable("recipe") {
            MenuListView(navController, favorites, onFavoritesChanged, ingredients)
        }

        composable(
            "recipeInfo/{recipeId}",
            arguments = listOf(
                navArgument("recipeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
            RecipeInfoView(navController, recipeId)
        }
    }
}



@Composable
fun HomeScreen(
    navController: NavController,
    initialImageUri: Uri?,
    onImageUriChanged: (Uri?) -> Unit,
    onUploadResponse: (String?) -> Unit,
    onLoadingChanged: (Boolean) -> Unit
) {
    var isCameraPreviewVisible by remember { mutableStateOf(false) }
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            onImageUriChanged(it)
        }
    }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                DisplayImage(initialImageUri)
                Spacer(modifier = Modifier.height(16.dp))
                CaptureImageButton { isCameraPreviewVisible = true }
                Spacer(modifier = Modifier.height(16.dp))
                PickImageButton { pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                Spacer(modifier = Modifier.height(16.dp))
                AnalyzeImageButton(navController, initialImageUri, context, isLoading, onUploadResponse) { isLoading = it }
            }
        }
    }
}


@Composable
fun DisplayImage(imageUri: Uri?) {
    Image(
        painter = rememberAsyncImagePainter(imageUri),
        contentDescription = "Captured Image",
        modifier = Modifier
            .fillMaxWidth()
            .size(320.dp)
            .padding(start = 16.dp, end = 16.dp),
        contentScale = ContentScale.Inside,
    )
}

@Composable
fun CaptureImageButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(start = 72.dp, end = 72.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = White,
            contentColor = Black // 원하는 텍스트 및 아이콘 색상
        ),
        border = BorderStroke(1.dp, Gray),
    ) {
        Icon(
            imageVector = Icons.Default.AddCircle,
            contentDescription = "Camera Icon",
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("재료 사진 찍기")
    }
}

@Composable
fun PickImageButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = White,
            contentColor = Black // 원하는 텍스트 및 아이콘 색상
        ),
        border = BorderStroke(1.dp, Gray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(start = 72.dp, end = 72.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AccountBox,
            contentDescription = "Select Gallery Image",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("갤러리")
    }
}
@Composable
fun AnalyzeImageButton(
    navController: NavController,
    initialImageUri: Uri?,
    context: Context,
    isLoading: Boolean,
    onUploadResponse: (String?) -> Unit,
    onLoadingChanged: (Boolean) -> Unit
) {
    Button(
        onClick = {
            initialImageUri?.let {
                onLoadingChanged(true)
                uploadImageToFlask(context, it) { uploadResponse ->
                    onUploadResponse(uploadResponse)
                    val encodedUri = Uri.encode(initialImageUri.toString())
                    navController.navigate("analysis/$encodedUri")
                    onLoadingChanged(false)
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = White,
            contentColor = Black // 원하는 텍스트 및 아이콘 색상
        ),
        border = BorderStroke(1.dp, Gray),
        enabled = initialImageUri != null && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(start = 32.dp, end = 32.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Black,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("분석 중...")
        } else {
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
