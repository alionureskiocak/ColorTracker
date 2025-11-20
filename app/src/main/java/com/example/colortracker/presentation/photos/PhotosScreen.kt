package com.example.colortracker.presentation.photos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.colortracker.domain.model.ColorEntity
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.model.FavoriteSwatch
import com.example.colortracker.presentation.composables.ColorSwatchItem
import com.example.colortracker.presentation.favorites.FavoriteViewModel
import java.io.File
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PhotosScreen(navController : NavHostController, viewModel: PhotosViewModel = hiltViewModel()) {

    val state by viewModel.uiState.collectAsState()
    val colorEntities = state.colorEntities
    val currentEntity = state.currentEntity
    val showDialog = state.showDialog
    val favoritesViewModel : FavoriteViewModel = hiltViewModel()
    val favoritesList by favoritesViewModel.favoritesList.collectAsState()

    var fullScreenSwatch by remember { mutableStateOf<ColorSwatchInfo?>(null) }

    if (fullScreenSwatch != null) {
        FullScreenColorDialog(
            sw = fullScreenSwatch!!,
            onDismiss = { fullScreenSwatch = null }
        )
    }

    if (showDialog){
        DialogScreen(
            navController = navController,
            viewModel = viewModel,
            entity = currentEntity,
            favoritesList = favoritesList,
            favoriteViewModel = favoritesViewModel,
            onFullScreen = { swatch ->
                fullScreenSwatch = swatch
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn {
            items(colorEntities) { entity ->
                ImageRow(entity){
                    viewModel.onImageSelected(entity)
                }
            }
        }
    }
}


@Composable
fun DialogScreen(
    navController : NavHostController,
    viewModel: PhotosViewModel,
    entity: ColorEntity,
    favoritesList : List<FavoriteSwatch>,
    favoriteViewModel: FavoriteViewModel,
    onFullScreen: (ColorSwatchInfo) -> Unit
) {
    AlertDialog(
        onDismissRequest = { viewModel.onDismiss() },
        title = {},
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = File(entity.colorPath)),
                        contentDescription = "Seçilen resim",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(entity.swatches) { sw ->
                        val isFavorite = favoritesList.any{it.hex == sw.hex}

                        ColorSwatchItem(
                            navController = navController,
                            sw = sw,
                            isFavorite = isFavorite,
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
                            onFullScreen = { selectedSwatch ->
                                onFullScreen(selectedSwatch)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { viewModel.onDismiss() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Kapat")
            }
        },
        dismissButton = {},
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
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
        )
    }
}

@Composable
fun ImageRow(entity : ColorEntity,onClick : (String) -> Unit) {

    val uri = entity.colorPath
    Image(
        painter = rememberAsyncImagePainter(model = File(uri)),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable {
                onClick(uri)
            }
    )
}


