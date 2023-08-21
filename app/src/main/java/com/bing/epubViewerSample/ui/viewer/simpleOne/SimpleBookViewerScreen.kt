package com.bing.epubViewerSample.ui.viewer.simpleOne

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bing.epubViewerSample.ui.common.composable.LoadingScreen
import com.bing.epubViewerSample.ui.common.viewer.ViewerControllerSeekbarWithPageIndexText
import com.bing.epubViewerSample.ui.viewer.BookViewerContract

/**
 * It's a simple book viewer screen.
 * replace it with [BookViewerSuccessContent] to see the difference.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimpleBookViewerSuccessScreen(
    uiData: BookViewerContract.UiData,
    uiInput: BookViewerContract.UiInput,
    onCloseClick: () -> Unit
) {
    var jumpPageRequire: Int? by remember { mutableStateOf(null) }
    var seekbarTotalPage: Int by remember { mutableStateOf(0) }
    var seekbarCurrentIndex: Int by remember { mutableStateOf(0) }
    var isLoading: Boolean by remember { mutableStateOf(true) }

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        SimpleBookViewerContent(
            uiData = uiData,
            onPageChange = { currentIndex, totalPage ->
                seekbarCurrentIndex = currentIndex
                seekbarTotalPage = totalPage
            },
            jumpPageRequireFromSeekBar = jumpPageRequire,
            onJumpPageRequireComplete = {
                jumpPageRequire = null
            },
            onLoadingStateChange = {
                isLoading = it
            }
        )
        SimpleTopContent(
            currentPage = seekbarCurrentIndex,
            totalPage = seekbarTotalPage,
            onChangeSeekbarProgressFinish = {
                jumpPageRequire = it
            }
        )

        if (isLoading) {
            LoadingScreen()
        }
    }
}

@Composable
fun SimpleTopContent(
    currentPage: Int,
    totalPage: Int,
    onChangeSeekbarProgressFinish: (globalIndex: Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ViewerControllerSeekbarWithPageIndexText(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            currentIndex = currentPage,
            totalPage = totalPage,
            onChangeSeekbarProgressFinish = onChangeSeekbarProgressFinish,
        )
    }
}