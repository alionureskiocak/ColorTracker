package com.example.colortracker.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.model.FavoriteSwatch
import com.example.colortracker.presentation.composables.ColorSwatchItem
import com.example.colortracker.presentation.photos.FullScreenColorDialog

// Not: Bu mapper fonksiyonunu uygun bir yere (domain model dosyası veya utils) taşıyabilirsiniz.
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
    navController: NavHostController, // Navigation gerekiyorsa ekleyin
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val favoritesList by viewModel.favoritesList.collectAsState()

    // Hangi rengin tam ekran olacağını tutan state
    var fullScreenSwatch by remember { mutableStateOf<ColorSwatchInfo?>(null) }

    // Tam ekran dialogu kontrolü
    if (fullScreenSwatch != null) {
        FullScreenColorDialog(
            sw = fullScreenSwatch!!,
            onDismiss = { fullScreenSwatch = null }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4) // Veya GridCells.Adaptive(120.dp)
        ) {
            items(favoritesList) { favSwatch ->
                // FavoriteSwatch'ı ColorSwatchInfo'ya çeviriyoruz
                val swatchInfo = favSwatch.toColorSwatchInfo()

                // Favori ekranında olduğumuz için isFavorite zaten true'dur
                // Ancak viewModel üzerinden kontrol etmek daha güvenlidir
                val isFavorite = viewModel.isFavorite(favSwatch)

                ColorSwatchItem(
                    navController = navController,
                    sw = swatchInfo,
                    isFavorite = isFavorite, // Genelde true döner
                    onAddToFavorites = { swatchToAdd ->
                        // Burada favoriden çıkarma işlemi yapılacaksa ViewModel'in
                        // onPress metodu toggle (ekle/çıkar) mantığında çalışıyorsa bu yeterlidir.
                        viewModel.onPress(swatchToAdd)
                    },
                    onFullScreen = { selectedSwatch ->
                        // Tıklanan rengi state'e atıyoruz, bu da Dialog'u tetikliyor
                        fullScreenSwatch = selectedSwatch
                    }
                )
            }
        }
    }
}