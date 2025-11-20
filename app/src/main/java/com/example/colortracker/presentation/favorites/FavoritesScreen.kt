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
import com.example.colortracker.presentation.composables.FavoriteSwatchItem

@Composable
fun FavoritesScreen(viewModel: FavoriteViewModel = hiltViewModel()) {

    val favoritesList = viewModel.favoritesList.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4)
        ) {
            items(favoritesList.value) {
                val isFavorite = viewModel.isFavorite(it)
                FavoriteSwatchItem(it,isFavorite, onAddToFavorites = {sw ->
                    viewModel.onPress(sw)
                }, onDismiss = {

                })

            }
        }
    }

}