package com.bing.epublib.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bing.epublib.ui.home.HomeScreen
import com.bing.epublib.ui.skyEpub.SkyEpubViewerScreen

fun NavGraphBuilder.addHomeTopLevel(
    navController: NavController
) {
    composable(ScreenRoutes.Home.route) {
        HomeScreen(
            onNavigateToSkyEpubViewer = {
                navController.navigate(ScreenRoutes.SkyEpubViewer.route)
            }
        )
    }
}

fun NavGraphBuilder.addSkyEpubViewerTopLevel(
    navController: NavController,
    onCloseClick: () -> Unit
) {
    composable(ScreenRoutes.SkyEpubViewer.route) {
        SkyEpubViewerScreen(onCloseClick = onCloseClick)
    }
}