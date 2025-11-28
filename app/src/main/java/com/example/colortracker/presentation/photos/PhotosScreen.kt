package com.example.colortracker.presentation.photos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.model.FavoriteSwatch
import com.example.colortracker.presentation.composables.ColorSwatchItem
import com.example.colortracker.presentation.favorites.FavoriteViewModel
import com.example.colortracker.presentation.palette.PaletteViewModel
import java.io.File

@Composable
fun PhotosScreen(
    navController: NavHostController,
    viewModel: PhotosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val colorEntities = state.colorEntities
    val currentEntity = state.currentEntity
    val showDialog = state.showDialog
    val favoritesViewModel: FavoriteViewModel = hiltViewModel()
    val favoritesList by favoritesViewModel.favoritesList.collectAsState()

    var fullScreenSwatch by remember { mutableStateOf<ColorSwatchInfo?>(null) }

    val paletteViewModel : PaletteViewModel = hiltViewModel()

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


    if (showDialog) {
        DialogScreen(
            navController = navController,
            viewModel = viewModel,
            entity = currentEntity,
            favoritesList = favoritesList,
            favoriteViewModel = favoritesViewModel,
            onFullScreen = { swatch ->
                fullScreenSwatch = swatch
            },
            paletteViewModel = paletteViewModel,
            onDelete = {
                viewModel.deleteColorEntity(currentEntity)
            }

        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {

            Text(
                text = "Gallery",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
            )

            if (colorEntities.isEmpty()) {

                EmptyGalleryState()
            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(colorEntities) { entity ->
                        PhotoGridItem(entity) {
                            viewModel.onImageSelected(entity)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyGalleryState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PhotoLibrary,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No photos yet",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Create a palette to save photos here.",
            color = Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun PhotoGridItem(entity: ColorEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f) // Dikdörtgen oranı
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(model = File(entity.colorPath)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )
        }
    }
}
@Composable
fun DialogScreen(
    navController: NavHostController,
    viewModel: PhotosViewModel,
    paletteViewModel : PaletteViewModel,
    entity: ColorEntity,
    favoritesList: List<FavoriteSwatch>,
    favoriteViewModel: FavoriteViewModel,
    onFullScreen: (ColorSwatchInfo) -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { viewModel.onDismiss() },
        containerColor = Color(0xFF16213E),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Palette",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = File(entity.colorPath)),
                            contentDescription = "Seçilen resim",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.1f))


                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(entity.swatches) { sw ->
                        val isFavorite = favoritesList.any { it.hex == sw.hex }

                        ColorSwatchItem(
                            navController = navController,
                            sw = sw,
                            isFavorite = isFavorite,
                            isFavoriteScreen = false,
                            onAddToFavorites = {
                                val fs = FavoriteSwatch(
                                    hex = sw.hex,
                                    rgb = sw.rgb,
                                    percentage = sw.percentage,
                                    titleTextColor = sw.titleTextColor,
                                    bodyTextColor = sw.bodyTextColor,
                                    population = sw.population
                                )
                                favoriteViewModel.onPress(fs)
                            },
                            showCoverage = true,
                            onFullScreen = { selectedSwatch ->
                                onFullScreen(selectedSwatch)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    var deleteFlag by remember { mutableStateOf(true) }
                    var iconText by remember { mutableStateOf("Delete") }
                    Button(
                        onClick = {
                            if (deleteFlag){
                                onDelete()
                                deleteFlag = !deleteFlag
                                iconText = "Undo"
                            } else{
                                paletteViewModel.insertSwatch(entity)
                                deleteFlag = !deleteFlag
                                iconText = "Delete"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE94560)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = iconText,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(iconText, color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { viewModel.onDismiss() }
            ) {
                Text("Close", color = Color.White.copy(alpha = 0.7f))
            }
        },
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp
    )
}

@Composable
fun FullScreenColorDialog(sw: ColorSwatchInfo, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(sw.rgb))
                .clickable { onDismiss() }
        ) {

            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close",
                tint = if (isLightColor(sw.rgb)) Color.Black.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(32.dp)
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.1f), CircleShape)
                    .padding(4.dp)
            )
        }
    }
}


fun isLightColor(color: Int): Boolean {
    val red = (color shr 16) and 0xFF
    val green = (color shr 8) and 0xFF
    val blue = color and 0xFF

    val luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue
    return luminance > 128
}