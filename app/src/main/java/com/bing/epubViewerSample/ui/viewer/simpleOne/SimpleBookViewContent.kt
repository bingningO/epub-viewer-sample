package com.bing.epubViewerSample.ui.viewer.simpleOne

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bing.epubViewerSample.ui.viewer.BookViewerContract
import com.bing.epubViewerSample.ui.viewer.androidView.BookViewerReflowable
import timber.log.Timber

@Composable
fun SimpleBookViewerContent(
    uiData: BookViewerContract.UiData,
    onPageChange: (currentIndex: Int, totalPage: Int) -> Unit,
    jumpPageRequireFromSeekBar: Int?,
    onJumpPageRequireComplete: () -> Unit,
    onLoadingStateChange: (isLoading: Boolean) -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { content ->
            BookViewerReflowable(content, 1, 24).apply {

                // init settings
                setBookPath(uiData.bookPath)
                setContentProvider(uiData.bookProvider)

                // sets up listeners for View -> Compose communication
                setLoadingListener {
                    onLoadingStateChange.invoke(it)
                }
                setScanListener(
                    scanFinishedListener = { totalPage, currentIndex ->
                        onPageChange(currentIndex, totalPage)
                    },
                    getNavListener = {}
                )
                setOnPageMovedListener {
                    onPageChange(it.currentIndexInBook, it.totalPage)
                }
            }
        },
        update = { viewer ->
            Timber.v("epub log update: $viewer")
            jumpPageRequireFromSeekBar?.let {
                val ppb = viewer.getPagePositionInBookByPageIndexInBook(it)
                viewer.gotoPageByPagePositionInBook(ppb)
                onJumpPageRequireComplete.invoke()
            }
        }
    )
}
