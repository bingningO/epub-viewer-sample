package com.bing.epublib.ui.common.viewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract

/**
 * @param onChangeSeekbarProgressFinish 0f ~ 1f
 */
@Composable
fun SeekBarContent(
    modifier: Modifier = Modifier,
    pagingInfo: SkyEpubViewerContract.BookPagingInfo,
    onChangeSeekbarProgressFinish: (globalIndex: Int) -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
    ) {
        // todo background
        // todo top bar - close button / table of content button / search button / bookmark button

        ViewerControllerSeekbarWithPageIndexText(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            pagingInfo = pagingInfo,
            onChangeSeekbarProgressFinish = onChangeSeekbarProgressFinish
        )
    }
}