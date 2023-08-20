package com.bing.epubViewerSample.ui.common.viewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bing.epubViewerSample.R
import com.bing.epubViewerSample.model.FontSize
import com.bing.epubViewerSample.ui.common.composable.SlidePanelWithTranslucentBackground
import com.bing.epubViewerSample.ui.viewer.BookViewerContract.ViewerIndexData
import com.bing.epubViewerSample.ui.viewer.BookViewerState

/**
 * Top content of the viewer screen for user to interact,
 * including seek bar, index, and other setting buttons.
 */
@Composable
fun <T> ViewerTopContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    onFontSizeSelected: (FontSize) -> Unit,
    bookViewerState: BookViewerState,
    indexList: List<ViewerIndexData<T>>,
) {
    Box(modifier = modifier.fillMaxSize()) {
        SeekBarContent(
            onClick = onClick,
            onCloseClick = onCloseClick,
            onIndexClick = {
                bookViewerState.updateTocVisible(true)
            },
            onFontSizeSelected = onFontSizeSelected,
            currentIndex = bookViewerState.stateData.currentIndex,
            totalPage = bookViewerState.stateData.totalPage,
            onChangeSeekbarProgressFinish = {
                bookViewerState.onSeekBarProgressChangeFinish(it)
            },
        )

        SlidePanelWithTranslucentBackground(
            modifier = Modifier.fillMaxSize(),
            isVisible = bookViewerState.stateData.isTOCVisible,
            title = stringResource(R.string.table_of_content),
            onHide = { bookViewerState.updateTocVisible(false) },
            content = {
                EpubViewerBookIndexContent(
                    indexList = indexList,
                    onIndexClick = {
                        bookViewerState.onBookIndexClick(it)
                    }
                )
            },
        )
    }
}
