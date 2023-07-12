package com.bing.epublib.ui.common.viewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bing.epublib.ui.theme.EpubLibTheme
import com.bing.epublib.ui.theme.Paddings

@Composable
internal fun <T> EpubViewerBookIndexContent(
    modifier: Modifier = Modifier,
    indexList: List<ViewerIndexData<T>>,
    onSelectPageIndex: (T) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            top = Paddings.x15,
            bottom = WindowInsets.safeContent.asPaddingValues()
                .calculateBottomPadding() + Paddings.x15,
        )
    ) {
        itemsIndexed(
            items = indexList,
            key = { index, item -> "${index}_${item.pageData.hashCode()}" }
        ) { _, indexData ->
            val displayTitle = buildString {
                repeat(indexData.nestLevel) {
                    append("    ")
                }
                append(indexData.indexTitle)
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectPageIndex.invoke(indexData.pageData) }
                    .padding(horizontal = 20.dp, vertical = Paddings.x075),
                text = displayTitle,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}

@Immutable
data class ViewerIndexData<T>(
    val indexTitle: String,
    val pageData: T,
    /**
     *  level of this index, 0 is the first-level
     */
    val nestLevel: Int,
)

@Preview
@Composable
private fun EpubViewerBookIndexContentPreview() {
    val dummyList = mutableListOf<ViewerIndexData<String>>().apply {
        repeat(10) {
            add(
                ViewerIndexData<String>(
                    indexTitle = "title $it",
                    pageData = "tittle $it",
                    nestLevel = 0
                )
            )
        }
    }
    EpubLibTheme() {
        EpubViewerBookIndexContent<String>(
            indexList = dummyList,
            onSelectPageIndex = {}
        )
    }
}
