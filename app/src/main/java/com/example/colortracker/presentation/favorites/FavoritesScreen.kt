package com.example.colortracker.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.model.FavoriteSwatch
import com.example.colortracker.presentation.composables.ColorSwatchItem
import com.example.colortracker.presentation.photos.FullScreenColorDialog


fun FavoriteSwatch.toColorSwatchInfo(): ColorSwatchInfo {
    return ColorSwatchInfo(
        hex = this.hex,
        rgb = this.rgb,
        population = this.population,
        percentage = this.percentage,
        titleTextColor = this.titleTextColor,
        bodyTextColor = this.bodyTextColor
    )
}

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val favoritesList by viewModel.favoritesList.collectAsState()


    var fullScreenSwatch by remember { mutableStateOf<ColorSwatchInfo?>(null) }


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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {

            Text(
                text = "Favorites",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
            )

            if (favoritesList.isEmpty()) {

                EmptyFavoritesState()
            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(favoritesList) { favSwatch ->

                        val swatchInfo = favSwatch.toColorSwatchInfo()


                        val isFavorite = viewModel.isFavorite(favSwatch)

                        ColorSwatchItem(
                            navController = navController,
                            sw = swatchInfo,
                            isFavorite = isFavorite,
                            isFavoriteScreen = true,
                            onAddToFavorites = { swatchToAdd ->
                                viewModel.onPress(swatchToAdd)
                            },
                            showCoverage = false,
                            onFullScreen = { selectedSwatch ->
                                fullScreenSwatch = selectedSwatch
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyFavoritesState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No favorites yet",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Tap the heart icon on any color\nto add it to your collection.",
            color = Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}