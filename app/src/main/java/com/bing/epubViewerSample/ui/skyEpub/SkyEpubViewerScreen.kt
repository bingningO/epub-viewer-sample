@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.bing.epubViewerSample.ui.skyEpub

import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.bing.epubViewerSample.model.FontSize.*
import com.bing.epubViewerSample.ui.common.composable.ErrorScreen
import com.bing.epubViewerSample.ui.common.composable.LoadingScreen
import com.bing.epubViewerSample.ui.common.viewer.ViewerTopContent
import com.bing.epubViewerSample.ui.skyEpub.SkyEpubViewerContract.*
import com.skytree.epub.NavPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun SkyEpubViewerScreen(
    viewModel: ViewModel = hiltViewModel<SkyEpubViewerViewModel>(),
    onCloseClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val uiInput = viewModel.uiInput

    uiState.events.firstOrNull()?.let { event ->
        LaunchedEffect(event.id) {
            Timber.v("epub log event: $event")
            when (event) {
                is SkyEpubViewerEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
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
    uiInput: UiInput,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SkyEpubViewerSuccessContent(
    uiData: UiData,
    uiInput: UiInput,
    onCloseClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val skyEpubViewerUiState = rememberSkyEpubViewerUiState<NavPoint>()

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        SkyEpubViewer(
            uiData = uiData,
            onLoadingStateChange = {
                scope.launch { uiInput.onLoadingStateChanged.emit(it) }
            },
            onPageChanged = {
                skyEpubViewerUiState.seekBarState.onPageInfoChanged(
                    it.currentIndexInBook,
                    it.totalPage
                )
                scope.launch {
                    uiInput.onChangePagePosition.emit(it.currentPositionInBook)
                }
            },
            skyEpubViewerUiState = skyEpubViewerUiState,
        )
    }

    AnimatedVisibility(
        visible = skyEpubViewerUiState.isShowTopContent,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        ViewerTopContent(
            onClick = {
                skyEpubViewerUiState.setShowTopContent(false)
            },
            onCloseClick = onCloseClick,
            skyEpubViewerUiState = skyEpubViewerUiState,
            onFontSizeSelected = {
                when (it) {
                    BIGGER -> {
                        scope.launch { uiInput.onClickFontSizeBigger.emit(Unit) }
                    }

                    SMALLER -> {
                        scope.launch { uiInput.onClickFontSizeSmaller.emit(Unit) }
                    }
                }
            }
        )
    }
}
