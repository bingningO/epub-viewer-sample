package com.bing.epubViewerSample.ui.common.viewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bing.epubViewerSample.R
import com.bing.epubViewerSample.model.FontSize
import com.bing.epubViewerSample.ui.common.composable.SlidePanelWithTranslucentBackground
import com.bing.epubViewerSample.ui.skyEpub.SkyEpubViewerUiState

@Composable
fun <T> ViewerTopContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    onFontSizeSelected: (FontSize) -> Unit,
    skyEpubViewerUiState: SkyEpubViewerUiState<T>,
) {
    Box {
        SeekBarContent(
            seekBarState = skyEpubViewerUiState.seekBarState,
            onClick = onClick,
            onCloseClick = onCloseClick,
            onIndexClick = {
                skyEpubViewerUiState.setTocVisible(true)
            },
            onFontSizeSelected = onFontSizeSelected
        )

        SlidePanelWithTranslucentBackground(
            modifier = Modifier.fillMaxSize(),
            isVisible = skyEpubViewerUiState.isTOCVisible,
            title = stringResource(R.string.table_of_content),
            onHide = { skyEpubViewerUiState.setTocVisible(false) },
            content = {
                EpubViewerBookIndexContent(
                    bookIndexState = skyEpubViewerUiState.bookIndexState,
                    onIndexClicked = {
                        skyEpubViewerUiState.onBookIndexClicked(it)
                    }
                )
            },
        )
    }
}
