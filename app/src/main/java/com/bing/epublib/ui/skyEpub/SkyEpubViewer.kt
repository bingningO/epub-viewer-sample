package com.bing.epublib.ui.skyEpub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.BookPagingInfo
import timber.log.Timber
import java.lang.ref.WeakReference

@Composable
internal fun SkyEpubViewer(
    modifier: Modifier = Modifier,
    uiData: SkyEpubViewerContract.UiData,
    onLoadingStateChange: (Boolean) -> Unit,
    onTap: () -> Unit,
    onGetMaxIndex: (Int) -> Unit,
    onPageChanged: (BookPagingInfo) -> Unit,
    requestJumpGlobalIndexProgress: Int?,
    onRequestJumpFinished: () -> Unit
) {
    var viewerRef by remember {
        mutableStateOf(WeakReference<SkyEpubReflowableViewer>(null))
    }
    val currentOnLoadingStateChange by rememberUpdatedState(onLoadingStateChange)
    val currentOnRequestPageFinished by rememberUpdatedState(onRequestJumpFinished)
    val currentOnTap by rememberUpdatedState(newValue = onTap)
    val currentOnPageChanged by rememberUpdatedState(newValue = onPageChanged)
    var maxIndex by remember { mutableStateOf(0) }

    AndroidView(
        modifier = modifier,
        factory = { factoryContext ->
            Timber.v("epub log viewer factory")
            SkyEpubReflowableViewer(factoryContext).apply {
                viewerRef = WeakReference(this)

                // init
                setBookPath(uiData.bookPath)
                setContentProvider(uiData.bookProvider)
                setStartPositionInBook(0f)

                // setListener, must call this to get totalPages by analysis global pagingInfo
                setPagingListener { max ->
                    maxIndex = (max - 1).coerceAtLeast(0)
                    onGetMaxIndex.invoke(maxIndex)
                }
                
                setLoadingListener {
                    currentOnLoadingStateChange.invoke(it)
                }
                setOnPageMovedListener { info ->
                    maxIndex = info.totalPage
                    currentOnPageChanged.invoke(info)
                }
                setOnScreenClicked {
                    currentOnTap.invoke()
                }

            }
        },
        update = { view ->
            Timber.v("epub log viewer update")
            requestJumpGlobalIndexProgress?.let {
                if (view.isPaging.not()) {
                    val ppb = view.getPagePositionInBookByPageIndexInBook(it)
                    // So absolute position in epub is expressed as pagePositionInBook.
                    // This is float value from 0.0f to 1.0f for entire book.
                    view.gotoPageByPagePositionInBook(ppb)

                    currentOnRequestPageFinished.invoke()
                }
            }
        }
    )

}