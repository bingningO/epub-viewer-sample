package com.bing.epubViewerSample.ui.navigation

sealed class ScreenRoutes(val route: String) {
    data object Home : ScreenRoutes("home")
    data object SkyEpubViewer : ScreenRoutes("skyEpubViewer")
}