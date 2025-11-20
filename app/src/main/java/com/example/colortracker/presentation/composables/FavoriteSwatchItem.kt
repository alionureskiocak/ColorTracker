package com.example.colortracker.presentation.composables

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.colortracker.domain.model.FavoriteSwatch
import com.example.colortracker.presentation.favorites.FavoriteViewModel
import kotlin.math.roundToInt

@Composable
fun FavoriteSwatchItem(
    sw: FavoriteSwatch,
    isFavorite : Boolean,
    onAddToFavorites: (FavoriteSwatch) -> Unit = {},
    onDismiss : () -> Unit = {}

    ) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var showDialog by remember { mutableStateOf(false) }

    val viewModel : FavoriteViewModel = hiltViewModel()
    val isFavorite = viewModel.isFavorite(sw)
    Card(
        modifier = Modifier
            .size(180.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(sw.rgb))
                .clickable { showDialog = true }
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

    // AlertDialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Color Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(200.dp)
                            .background(
                                Color(sw.rgb),
                                RoundedCornerShape(8.dp)
                            )
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Sağ taraf - Renk kodları ve favoriye ekleme
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(200.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Renk kodları
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // HEX kodu
                            Text(
                                text = "HEX",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        clipboard.setText(AnnotatedString(sw.hex))
                                        Toast
                                            .makeText(
                                                context,
                                                "HEX copied",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sw.hex,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // RGB kodu
                            Text(
                                text = "RGB",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val rgb = String.format(
                                "rgb(%d, %d, %d)",
                                (sw.rgb shr 16) and 0xFF,
                                (sw.rgb shr 8) and 0xFF,
                                sw.rgb and 0xFF
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        clipboard.setText(AnnotatedString(rgb))
                                        Toast
                                            .makeText(
                                                context,
                                                "RGB copied",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = rgb,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Yüzde bilgisi
                            Text(
                                text = "Coverage",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(sw.percentage * 100).roundToInt()}%",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        Button(
                            onClick = {
                                onAddToFavorites(sw)

                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            //Text("Add to Favorites")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDismiss()
                }) {
                    Text("Close")
                }
            }
        )
    }
}