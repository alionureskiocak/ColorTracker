package com.example.colortracker.presentation.composables

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.colortracker.domain.model.ColorSwatchInfo
import com.example.colortracker.domain.model.FavoriteSwatch
import kotlin.math.roundToInt

@Composable
fun ColorSwatchItem(
    navController: NavHostController,
    sw: ColorSwatchInfo,
    isFavorite: Boolean,
    showCoverage : Boolean,
    isFavoriteScreen: Boolean = false,
    onAddToFavorites: (FavoriteSwatch) -> Unit = {},
    onFullScreen: (ColorSwatchInfo) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var showDialog by remember { mutableStateOf(false) }


    Card(
        modifier = Modifier
            .size(120.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(sw.rgb)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { showDialog = true }
        ) {


            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = sw.hex,
                    color = Color(sw.titleTextColor),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                if (!isFavoriteScreen) {
                    Text(
                        text = "${(sw.percentage * 100).roundToInt()}%",
                        color = Color(sw.bodyTextColor),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF16213E),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Color Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(sw.rgb))
                            .clickable {
                                onFullScreen(sw)
                            }
                    )


                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(220.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            DetailLabel("HEX")
                            DetailRow(sw.hex) {
                                clipboard.setText(AnnotatedString(sw.hex))
                                Toast.makeText(context, "HEX copied", Toast.LENGTH_SHORT).show()
                            }

                            Spacer(modifier = Modifier.height(12.dp))


                            DetailLabel("RGB")
                            val rgb = "rgb(${(sw.rgb shr 16) and 0xFF}, ${(sw.rgb shr 8) and 0xFF}, ${sw.rgb and 0xFF})"
                            DetailRow(rgb, fontSize = 13.sp) {
                                clipboard.setText(AnnotatedString(rgb))
                                Toast.makeText(context, "RGB copied", Toast.LENGTH_SHORT).show()
                            }

                            Spacer(modifier = Modifier.height(12.dp))


                            DetailLabel("Coverage")
                            Text(
                                text = if (showCoverage) "${(sw.percentage * 100).roundToInt()}%" else "-",
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 1. SİLME BUTONU (Küçük, Kırmızı)
                            Button(
                                onClick = {
                                    // todoo
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }


                            Button(
                                onClick = {
                                    val fs = FavoriteSwatch(
                                        hex = sw.hex,
                                        rgb = sw.rgb,
                                        percentage = sw.percentage,
                                        titleTextColor = sw.titleTextColor,
                                        bodyTextColor = sw.bodyTextColor,
                                        population = sw.population
                                    )
                                    onAddToFavorites(fs)
                                },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE94560)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(
                        text = "Close",
                        color = Color(0xFFE94560),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        )
    }
}



@Composable
fun DetailLabel(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.6f)
    )
}

@Composable
fun DetailRow(text: String, fontSize: androidx.compose.ui.unit.TextUnit = 15.sp, onCopy: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable { onCopy() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Medium,
            fontSize = fontSize,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ContentCopy,
            contentDescription = "Copy",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(14.dp)
        )
    }
}
