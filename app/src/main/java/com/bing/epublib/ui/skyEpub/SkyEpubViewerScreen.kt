@file:OptIn(ExperimentalFoundationApi::class)

package com.bing.epublib.ui.skyEpub

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.bing.epublib.ui.common.composable.ErrorScreen
import com.bing.epublib.ui.common.composable.LoadingScreen
import com.bing.epublib.ui.common.viewer.SeekBarContent
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiData
import kotlinx.coroutines.launch

@Composable
fun SkyEpubViewerScreen(
    viewModel: SkyEpubViewerContract.ViewModel = hiltViewModel<SkyEpubViewerViewModel>(),
) {
    val uiState = viewModel.uiState
    val uiInput = viewModel.uiInput

    uiState.events.firstOrNull()?.let { event ->
        LaunchedEffect(event.id) {
            // todo when(event)
            uiInput.onEventConsumed.emit(event)
        }
    }
    SkyEpubViewerContent(
        uiData = uiState.uiData,
        uiInput = uiInput,
    )
}

@Composable
private fun SkyEpubViewerContent(
    uiData: UiData,
    uiInput: SkyEpubViewerContract.UiInput,
) {
    when {
//        uiData.isInitLoading -> {
//            // loading for preparing book data
//            LoadingScreen()
//        }

        uiData.error != null -> {
            ErrorScreen(error = uiData.error!!)
        }

        else -> {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiData.bookProvider != null) {
                    SkyEpubViewerSuccessContent(
                        uiData = uiData, uiInput = uiInput
                    )
                }
                
                if (uiData.isLoading) {
                    LoadingScreen()
                }
            }
        }
    }
}

@Composable
private fun SkyEpubViewerSuccessContent(
    uiData: UiData,
    uiInput: SkyEpubViewerContract.UiInput
) {
    val scope = rememberCoroutineScope()
    var isShowController by remember { mutableStateOf(false) }
    var maxIndexFromViewerSDK by remember { mutableStateOf(0) }
    var currentGlobalIndexFromViewerSDK by remember { mutableStateOf(0) }
    var progressChangeRequestByController: Int? by remember { mutableStateOf(null) }

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        SkyEpubViewer(
            uiData = uiData,
            onLoadingStateChange = {
                scope.launch { uiInput.onLoadingStateChanged.emit(it) }
            },
            requestJumpGlobalIndexProgress = progressChangeRequestByController,
            onTap = {
                isShowController = true
            },
            onPageChanged = {
                maxIndexFromViewerSDK = it.totalPage
                currentGlobalIndexFromViewerSDK = it.currentIndexInBook
            },
            onGetTotalPages = {
                maxIndexFromViewerSDK = it
            },
            onRequestJumpFinished = {
                progressChangeRequestByController = null
            }
        )
    }

    AnimatedVisibility(
        visible = isShowController,
        enter = fadeIn(),
        exit = fadeOut()
    ) {

        SeekBarContent(currentIndex = currentGlobalIndexFromViewerSDK,
            totalPage = maxIndexFromViewerSDK,
            onChangeSeekbarProgressFinish = {
                progressChangeRequestByController = it
            },
            onClick = {
                isShowController = false
            })
    }
}
