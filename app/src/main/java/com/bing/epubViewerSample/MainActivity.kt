@file:OptIn(ExperimentalFoundationApi::class)

package com.bing.epubViewerSample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.bing.epubViewerSample.ui.navigation.ScreenRoutes
import com.bing.epubViewerSample.ui.navigation.addHomeTopLevel
import com.bing.epubViewerSample.ui.navigation.addSkyEpubViewerTopLevel
import com.bing.epubViewerSample.ui.theme.epubViewerSampleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            epubViewerSampleTheme {
                NavHost(
                    navController = navController,
                    startDestination = ScreenRoutes.Home.route
                ) {
                    addHomeTopLevel(navController)
                    addSkyEpubViewerTopLevel(
                        navController
                    )
                }
            }
        }
    }
}
