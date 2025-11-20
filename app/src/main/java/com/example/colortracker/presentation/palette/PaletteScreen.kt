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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.colortracker.domain.model.FavoriteSwatch
import com.example.colortracker.presentation.composables.ColorSwatchItem
import com.example.colortracker.presentation.favorites.FavoriteViewModel
import java.io.File

@Composable
fun PaletteScreen(navController: NavHostController, viewModel: PaletteViewModel = hiltViewModel()) {

    val state by viewModel.uiState.collectAsState()
    val error = state.error
    val colorEntites = state.colorEntities
    val currentBitmap = state.bitmap
    val swatches = state.swatches
    val favoritesViewModel : FavoriteViewModel = hiltViewModel()
    val favoritesList by favoritesViewModel.favoritesList.collectAsState()

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
            viewModel.updateBitmap(bitmap!!)
            viewModel.analyzeBitmap(bitmap)
        }
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(photoUri)
        } else {
            Toast.makeText(context, "Kamera izni gerekli.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter

    ) {
        state.error?.let { Text("Hata: $it", color = Color.Red) }


        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
        else if(currentBitmap==null){
            ChoiceScreen(galleryLauncher,permissionLauncher)
        }
        else{
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentBitmap.let {
                    Image(bitmap = currentBitmap.asImageBitmap(), contentDescription = null,
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.padding(top = 16.dp)) {
                    items(state.swatches) { sw ->
                        val isFavorite = favoritesList.any{it.hex == sw.hex}
                        ColorSwatchItem(navController,sw,isFavorite){
                            val fs = FavoriteSwatch(
                                hex = sw.hex,
                                rgb = sw.rgb,
                                percentage = sw.percentage,
                                titleTextColor = sw.titleTextColor,
                                bodyTextColor = sw.bodyTextColor,
                                population = sw.population
                            )
                            favoritesViewModel.onPress(fs)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChoiceScreen(
    galleryLauncher : ManagedActivityResultLauncher<String, Uri?>,
    permissionLauncher : ManagedActivityResultLauncher<String, Boolean>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Galeriden seç")
        }
        Spacer(Modifier.width(8.dp))
        Button(onClick = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }) {
            Text("Kameradan çek")
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
