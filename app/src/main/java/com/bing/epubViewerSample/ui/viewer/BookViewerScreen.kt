@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.bing.epubViewerSample.ui.viewer

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
import com.bing.epubViewerSample.ui.viewer.BookViewerContract.*
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun BookViewerScreen(
    viewModel: ViewModel = hiltViewModel<BookViewerViewModel>(),
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
            uiInput.onEventConsume.emit(event)
        }
    }
    uiState.uiData.let { uiData ->
        when {
            uiData.error != null -> {
                ErrorScreen(error = uiData.error!!)
            }

            else -> {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (uiData.bookProvider != null) {
                        BookViewerSuccessContent(
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
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookViewerSuccessContent(
    uiData: UiData,
    uiInput: UiInput,
    onCloseClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bookViewerState = rememberBookViewerState()

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        SkyEpubViewer(
            uiData = uiData,
            bookViewerState = bookViewerState,
            onLoadingStateChange = {
                scope.launch { uiInput.onLoadingStateChange.emit(it) }
            },
            onPageChange = {
                scope.launch {
                    uiInput.onChangePagePosition.emit(it.currentPositionInBook)
                }
            },
            onIndexDataLoad = {
                scope.launch { uiInput.onIndexDataLoad.emit(it) }
            }
        )
    }

    AnimatedVisibility(
        visible = bookViewerState.isShowTopContent,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        ViewerTopContent(
            onClick = {
                bookViewerState.updateShowTopContent(false)
            },
            onCloseClick = onCloseClick,
            bookViewerState = bookViewerState,
            indexList = uiData.indexList,
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
