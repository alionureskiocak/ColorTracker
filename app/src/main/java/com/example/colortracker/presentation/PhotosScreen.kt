package com.example.colortracker.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun PhotosScreen(viewModel: PaletteViewModel = hiltViewModel()) {

    val state by viewModel.uiState.collectAsState()
    val colorEntities = state.colorEntities
    val photoUris = colorEntities.map { it.colorPath }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn {
            items(photoUris) { uri ->
                println(uri)
                Image(
                    painter = rememberAsyncImagePainter(model = File(uri)),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }

        }
    }

}