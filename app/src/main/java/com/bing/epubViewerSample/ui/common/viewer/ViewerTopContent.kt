package com.bing.epubViewerSample.ui.common.viewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bing.epubViewerSample.R
import com.bing.epubViewerSample.model.FontSize
import com.bing.epubViewerSample.ui.common.composable.SlidePanelWithTranslucentBackground
import com.bing.epubViewerSample.ui.viewer.BookViewerUiState

@Composable
fun <T> ViewerTopContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    onFontSizeSelected: (FontSize) -> Unit,
    bookViewerUiState: BookViewerUiState<T>,
) {
    Box {
        SeekBarContent(
            seekBarState = bookViewerUiState.seekBarState,
            onClick = onClick,
            onCloseClick = onCloseClick,
            onIndexClick = {
                bookViewerUiState.updateTocVisible(true)
            },
            onFontSizeSelected = onFontSizeSelected
        )

        SlidePanelWithTranslucentBackground(
            modifier = Modifier.fillMaxSize(),
            isVisible = bookViewerUiState.isTOCVisible,
            title = stringResource(R.string.table_of_content),
            onHide = { bookViewerUiState.updateTocVisible(false) },
            content = {
                EpubViewerBookIndexContent(
                    bookIndexState = bookViewerUiState.bookIndexState,
                    onIndexClicked = {
                        bookViewerUiState.onBookIndexClicked(it)
                    }
                )
            },
        )
    }
}
