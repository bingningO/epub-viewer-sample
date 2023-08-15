package com.bing.epubViewerSample.ui.common.viewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bing.epublib.R
import com.bing.epublib.model.FontSize
import com.bing.epublib.ui.common.composable.SlidePanelWithTranslucentBackground

@Composable
fun <T> ViewerTopContent(
    modifier: Modifier = Modifier,
    currentIndex: Int,
    totalPage: Int,
    onChangeSeekbarProgressFinish: (globalIndex: Int) -> Unit,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    navList: List<ViewerIndexData<T>>,
    onNavItemClick: (T) -> Unit,
    onFontSizeSelected: (FontSize) -> Unit,
) {
    var isSlidePaneVisible by remember {
        mutableStateOf(false)
    }

    Box {
        SeekBarContent(
            currentIndex = currentIndex,
            totalPage = totalPage,
            onChangeSeekbarProgressFinish = onChangeSeekbarProgressFinish,
            onClick = onClick,
            onCloseClick = onCloseClick,
            onIndexClick = {
                isSlidePaneVisible = true
            },
            onFontSizeSelected = onFontSizeSelected
        )

        SlidePanelWithTranslucentBackground(
            modifier = Modifier.fillMaxSize(),
            isVisible = isSlidePaneVisible,
            title = stringResource(R.string.table_of_content),
            onHide = { isSlidePaneVisible = false },
            content = {
                EpubViewerBookIndexContent(
                    indexList = navList,
                    onSelectPageIndex = onNavItemClick
                )
            },
        )
    }
}
