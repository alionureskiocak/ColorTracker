package com.example.colortracker.presentation.palette

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.model.FavoriteSwatch
import com.example.colortracker.presentation.composables.ColorSwatchItem
import com.example.colortracker.presentation.favorites.FavoriteViewModel
import com.example.colortracker.presentation.photos.FullScreenColorDialog
import java.io.File

@Composable
fun PaletteScreen(
    navController: NavHostController,
    viewModel: PaletteViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val currentBitmap = state.bitmap
    val favoritesViewModel: FavoriteViewModel = hiltViewModel()
    val favoritesList by favoritesViewModel.favoritesList.collectAsState()

    var fullScreenSwatch by remember { mutableStateOf<ColorSwatchInfo?>(null) }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = uriToBitmap(context, it) ?: return@let
            viewModel.updateBitmap(bitmap)
            viewModel.analyzeBitmap(bitmap)
        }
    }

    val photoUri = remember {
        val file = File(context.externalCacheDir, "temp_photo.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val bitmap = uriToBitmap(context, photoUri)
            bitmap?.let {
                viewModel.updateBitmap(it)
                viewModel.analyzeBitmap(it)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(photoUri)
        } else {
            Toast.makeText(context, "Need Camera Permission.", Toast.LENGTH_SHORT).show()
        }
    }


    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E),
            Color(0xFF0F3460)
        )
    )

    if (fullScreenSwatch != null) {
        FullScreenColorDialog(
            sw = fullScreenSwatch!!,
            onDismiss = { fullScreenSwatch = null }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {

        AnimatedContent(
            targetState = currentBitmap,
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(600))
            },
            label = "ScreenTransition"
        ) { bitmap ->
            if (bitmap == null) {

                EmptyStateScreen(galleryLauncher, permissionLauncher)
            } else {
                ResultScreen(
                    bitmap = bitmap,
                    swatches = state.swatches,
                    favoritesList = favoritesList,
                    isLoading = state.isLoading,
                    error = state.error,
                    navController = navController,
                    onFavoriteClick = { sw ->
                        val fs = FavoriteSwatch(
                            hex = sw.hex,
                            rgb = sw.rgb,
                            percentage = sw.percentage,
                            titleTextColor = sw.titleTextColor,
                            bodyTextColor = sw.bodyTextColor,
                            population = sw.population
                        )
                        favoritesViewModel.onPress(fs)
                    },
                    onFullScreenClick = { sw ->
                        fullScreenSwatch = sw
                    },
                    onBackClick = {
                        viewModel.resetState()
                    },
                    galleryLauncher = galleryLauncher
                )
            }
        }
    }
}

@Composable
fun EmptyStateScreen(
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Brush.linearGradient(listOf(Color(0xFFE94560), Color(0xFF0F3460))),
                    shape = CircleShape
                )
                .shadow(10.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ColorLens,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Create a Color Palette",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Upload an Image explore the dominant colors.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))


        ModernButton(
            text = "Choose From Gallery",
            icon = Icons.Filled.AddPhotoAlternate,
            onClick = { galleryLauncher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ModernButton(
            text = "Take a Photo",
            icon = Icons.Filled.CameraAlt,
            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            isOutlined = true
        )
    }
}

@Composable
fun ResultScreen(
    bitmap: Bitmap,
    swatches: List<ColorSwatchInfo>,
    favoritesList: List<FavoriteSwatch>,
    isLoading: Boolean,
    error: String?,
    navController: NavHostController,
    onFavoriteClick: (ColorSwatchInfo) -> Unit,
    onFullScreenClick: (ColorSwatchInfo) -> Unit,
    onBackClick: () -> Unit,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
                .padding(16.dp)
        ) {

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }


            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .size(40.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }


            SmallFloatingActionButton(
                onClick = { galleryLauncher.launch("image/*") },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Image,
                    contentDescription = "Change",
                    tint = Color.White
                )
            }
        }


        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray.copy(alpha = 0.4f))
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    error?.let {
                        Text("Error: $it", color = MaterialTheme.colorScheme.error)
                    }

                    Text(
                        text = "Palette",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(swatches) { sw ->
                            val isFavorite = favoritesList.any { it.hex == sw.hex }

                            ColorSwatchItem(
                                navController = navController,
                                sw = sw,
                                isFavorite = isFavorite,
                                isFavoriteScreen = false,
                                onAddToFavorites = { onFavoriteClick(sw) },
                                showCoverage = true,
                                onFullScreen = { onFullScreenClick(sw) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isOutlined: Boolean = false
) {
    val buttonModifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .shadow(if (isOutlined) 0.dp else 8.dp, RoundedCornerShape(16.dp))

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = buttonModifier,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE94560)
            )
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}


fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val src = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(src) { decoder, _, _ ->
            decoder.isMutableRequired = false
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}