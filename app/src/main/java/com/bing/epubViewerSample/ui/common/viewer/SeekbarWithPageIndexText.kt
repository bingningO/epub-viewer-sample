package com.bing.epubViewerSample.ui.common.viewer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bing.epubViewerSample.ui.theme.epubViewerSampleTheme

@Composable
internal fun ViewerControllerSeekbarWithPageIndexText(
    modifier: Modifier = Modifier,
    currentIndex: Int,
    totalPage: Int,
    onChangeSeekbarProgressFinish: (globalIndex: Int) -> Unit,
) {
    var displayProgress: Float by remember(currentIndex) {
        mutableStateOf(
            currentIndex.toFloat() + 1
        )
    }
    val maxIndex by remember(totalPage) {
        mutableStateOf(totalPage.toFloat())
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val displayMax = if (maxIndex > 0) "${maxIndex.toInt()}" else "..."
        Text(
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center,
            text = "${displayProgress.toInt()} / $displayMax",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
        )

        Slider(
            value = displayProgress,
            onValueChange = { progress ->
                displayProgress = progress
            },
            valueRange = 0f..if (maxIndex > 0) maxIndex else 100f,
            onValueChangeFinished = {
                onChangeSeekbarProgressFinish.invoke(displayProgress.toInt())
            },
        )
    }
}

@Preview
@Composable
private fun PreviewViewerControllerSeekbarWithPageIndexTextUBook() {
    epubViewerSampleTheme {
        ViewerControllerSeekbarWithPageIndexText(
            modifier = Modifier,
            currentIndex = 17,
            totalPage = 120,
            onChangeSeekbarProgressFinish = {},
        )
    }
}
