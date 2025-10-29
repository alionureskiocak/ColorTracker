package com.example.colortracker.presentation

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.colortracker.domain.model.ColorSwatchInfo
import kotlin.math.roundToInt

@Composable
fun PaletteScreen(viewModel: PaletteViewModel = hiltViewModel()) {

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = uriToBitmap(context, it) ?: return@let
            currentBitmap = bitmap
            viewModel.analyzeBitmap(bitmap)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.analyzeBitmap(bitmap)
            currentBitmap = bitmap
        } else {
            Toast.makeText(context, "Kamera sonucu alınamadı.", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
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
        Row {
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
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        state.error?.let { Text("Hata: $it", color = Color.Red) }

        Column {
            currentBitmap?.let {
                Image(bitmap = currentBitmap!!.asImageBitmap(), contentDescription = null)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(top = 16.dp)) {
                items(state.colors) { sw ->
                    ColorSwatchItem(sw)
                }
            }
        }

    }
}

@Composable
fun ColorSwatchItem(sw: ColorSwatchInfo) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(sw.rgb))
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = sw.hex,
                    color = Color(sw.titleTextColor),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(sw.percentage * 100).roundToInt()}%",
                    color = Color(sw.bodyTextColor)
                )
            }
        }
    }
}

// 🔄 URI → Bitmap dönüştürücü
fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val src = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(src) { decoder, _, _ ->
                decoder.isMutableRequired = false
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}