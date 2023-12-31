package com.bing.epubViewerSample.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bing.epubViewerSample.ui.home.HomeScreen
import com.bing.epubViewerSample.ui.viewer.BookViewerScreen

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
) {
    composable(ScreenRoutes.SkyEpubViewer.route) {
        BookViewerScreen(onCloseClick = {
            navController.popBackStack()
        })
    }
}