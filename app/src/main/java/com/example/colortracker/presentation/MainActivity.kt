package com.example.colortracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.colortracker.domain.model.NavigationItem
import com.example.colortracker.presentation.color_screen.ColorScreen
import com.example.colortracker.presentation.favorites.FavoritesScreen
import com.example.colortracker.presentation.palette.PaletteScreen
import com.example.colortracker.presentation.photos.PhotosScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            PaletteScreen(navController =navController )
            SetUpNavigation(navController)

        }
    }
}

@Composable
fun SetUpNavigation(navController : NavHostController) {

    val items = listOf<NavigationItem>(
        NavigationItem("MainScreen","Main", Icons.Filled.Home,Icons.Outlined.Home),
        NavigationItem("PhotosScreen","Photos",Icons.Filled.ThumbUp,Icons.Outlined.ThumbUp),
        NavigationItem("FavoritesScreen","Favorites",Icons.Filled.Favorite,Icons.Outlined.FavoriteBorder)
    )


    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                var currentRoute = navBackStackEntry?.destination?.route
                if (currentRoute!="ColorScreen"){
                    NavigationBar {
                        items.forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                label = { Text(item.title) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == item.route)
                                            item.selectedIcon
                                        else item.unselectedIcon,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }

            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "MainScreen",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("MainScreen") { PaletteScreen(navController) }
                composable("PhotosScreen") { PhotosScreen(navController) }
                composable("FavoritesScreen") { FavoritesScreen() }
                //composable(route = "ColorScreen/{rgb}") { backStackEntry ->
                //    val rgb = backStackEntry.arguments?.getString("rgb")?.toInt() ?: -1
                //    ColorScreen(color = rgb) }
            }
        }

    }
}
