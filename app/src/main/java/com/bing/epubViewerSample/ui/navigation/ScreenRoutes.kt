package com.bing.epubViewerSample.ui.navigation

sealed class ScreenRoutes(val route: String) {
    object Home : ScreenRoutes("home")
    object SkyEpubViewer : ScreenRoutes("skyEpubViewer")
}