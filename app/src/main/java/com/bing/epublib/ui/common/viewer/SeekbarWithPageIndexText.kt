package com.bing.epublib.ui.common.viewer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract
import com.bing.epublib.ui.theme.EpubLibTheme

@Composable
internal fun ViewerControllerSeekbarWithPageIndexText(
    modifier: Modifier = Modifier,
    pagingInfo: SkyEpubViewerContract.BookPagingInfo,
    onChangeSeekbarProgressFinish: (globalIndex: Int) -> Unit,
) {
    var displayProgress: Float by remember(pagingInfo.currentIndexInBook) {
        mutableStateOf(
            pagingInfo.currentIndexInBook.toFloat()
        )
    }
    val maxIndex by remember(pagingInfo.totalPage) {
        derivedStateOf {
            (pagingInfo.totalPage.toFloat() - 1).coerceAtLeast(1f)
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center,
            text = "${displayProgress.toInt()} / ${maxIndex.toInt()}\nChapter ${pagingInfo.currentChapterIndex}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
        )

        Slider(
            value = displayProgress,
            onValueChange = { progress ->
                displayProgress = progress
            },
            valueRange = 0f..maxIndex,
            onValueChangeFinished = {
                onChangeSeekbarProgressFinish.invoke(displayProgress.toInt())
            },
        )
    }
}

@Preview
@Composable
private fun PreviewViewerControllerSeekbarWithPageIndexTextUBook() {
    EpubLibTheme {
        ViewerControllerSeekbarWithPageIndexText(
            pagingInfo = SkyEpubViewerContract.BookPagingInfo(
                totalPageInChapter = 10,
                currentIndexInChapter = 5,
                currentIndexInBook = 5,
                currentChapterIndex = 1,
                totalNumberOfChapters = 10
            ),
            onChangeSeekbarProgressFinish = {},
        )
    }
}
