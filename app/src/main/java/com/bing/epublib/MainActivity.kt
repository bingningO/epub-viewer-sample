@file:OptIn(ExperimentalFoundationApi::class)

package com.bing.epublib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.bing.epublib.ui.navigation.ScreenRoutes
import com.bing.epublib.ui.navigation.addHomeTopLevel
import com.bing.epublib.ui.navigation.addSkyEpubViewerTopLevel
import com.bing.epublib.ui.theme.EpubLibTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            EpubLibTheme {
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
