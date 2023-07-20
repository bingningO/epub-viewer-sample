@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

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
import com.bing.epublib.ui.common.viewer.ViewerIndexData
import com.bing.epublib.ui.common.viewer.ViewerTopContent
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiData
import com.skytree.epub.NavPoint
import kotlinx.coroutines.launch

@Composable
fun SkyEpubViewerScreen(
    viewModel: SkyEpubViewerContract.ViewModel = hiltViewModel<SkyEpubViewerViewModel>(),
    onCloseClick: () -> Unit
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
        onCloseClick = onCloseClick
    )
}

@Composable
private fun SkyEpubViewerContent(
    uiData: UiData,
    uiInput: SkyEpubViewerContract.UiInput,
    onCloseClick: () -> Unit
) {
    when {
        uiData.error != null -> {
            ErrorScreen(error = uiData.error!!)
        }

        else -> {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiData.bookProvider != null) {
                    SkyEpubViewerSuccessContent(
                        uiData = uiData, uiInput = uiInput, onCloseClick = onCloseClick
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
    uiInput: SkyEpubViewerContract.UiInput,
    onCloseClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isShowTopContent by remember { mutableStateOf(false) }
    var maxIndexFromViewerSDK: Int by remember { mutableStateOf(0) }
    var currentGlobalIndexFromViewerSDK: Int by remember(
        uiData.initialPositionInBook,
        maxIndexFromViewerSDK
    ) {
        mutableStateOf(
            if (maxIndexFromViewerSDK != 0) {
                (uiData.initialPositionInBook * maxIndexFromViewerSDK).toInt()
            } else {
                0
            }
        )
    }
    var progressChangeRequestByController: Int? by remember { mutableStateOf(null) }
    var navChangeRequestByToc: NavPoint? by remember { mutableStateOf(null) }
    var navListData: List<ViewerIndexData<NavPoint>> by remember {
        mutableStateOf(emptyList())
    }

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        SkyEpubViewer(
            uiData = uiData,
            onLoadingStateChange = {
                scope.launch { uiInput.onLoadingStateChanged.emit(it) }
            },
            requestJumpGlobalIndexProgress = progressChangeRequestByController,
            requestJumpNav = navChangeRequestByToc,
            onTap = {
                isShowTopContent = true
            },
            onPageChanged = {
                maxIndexFromViewerSDK = it.totalPage
                currentGlobalIndexFromViewerSDK = it.currentIndexInBook
                scope.launch {
                    uiInput.onChangePagePosition.emit(it.currentPositionInBook)
                }
            },
            onGetTotalPages = {
                maxIndexFromViewerSDK = it
            },
            onRequestJumpFinished = {
                progressChangeRequestByController = null
                navChangeRequestByToc = null
            },
            onGetNavList = {
                navListData = it
            },
        )
    }

    AnimatedVisibility(
        visible = isShowTopContent,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        ViewerTopContent<NavPoint>(
            currentIndex = currentGlobalIndexFromViewerSDK,
            totalPage = maxIndexFromViewerSDK,
            onChangeSeekbarProgressFinish = {
                progressChangeRequestByController = it
            },
            onClick = {
                isShowTopContent = false
            },
            onCloseClick = onCloseClick,
            navList = navListData,
            onNavItemClick = {
                isShowTopContent = false
                navChangeRequestByToc = it
            }
        )
    }
}
