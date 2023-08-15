package com.bing.epubViewerSample.ui.skyEpub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.bing.epublib.ui.common.viewer.ViewerIndexData
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.BookPagingInfo
import com.skytree.epub.NavPoint
import com.skytree.epub.SkyActivityState
import timber.log.Timber
import java.lang.ref.WeakReference

@Composable
internal fun SkyEpubViewer(
    modifier: Modifier = Modifier,
    uiData: SkyEpubViewerContract.UiData,
    onLoadingStateChange: (Boolean) -> Unit,
    onTap: () -> Unit,
    onGetTotalPages: (Int) -> Unit,
    onGetNavList: (List<ViewerIndexData<NavPoint>>) -> Unit,
    onPageChanged: (BookPagingInfo) -> Unit,
    requestJumpGlobalIndexProgress: Int?,
    requestJumpNav: NavPoint?,
    onRequestJumpFinished: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var viewerRef by remember {
        mutableStateOf(WeakReference<SkyEpubReflowableControl>(null))
    }
    val currentOnLoadingStateChange by rememberUpdatedState(onLoadingStateChange)
    val currentOnRequestPageFinished by rememberUpdatedState(onRequestJumpFinished)
    val currentOnTap by rememberUpdatedState(newValue = onTap)
    val currentOnPageChanged by rememberUpdatedState(newValue = onPageChanged)
    val currentOnGetTotalPagesChanged by rememberUpdatedState(newValue = onGetTotalPages)
    var maxIndex by remember { mutableStateOf(0) }
    var isInitLoading by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                // better call onStart(), onStop(), destroy() along with UI lifecycle depending on SDK requirement
                Lifecycle.Event.ON_START -> viewerRef.get()?.activityState =
                    SkyActivityState.Started

                Lifecycle.Event.ON_STOP -> viewerRef.get()?.activityState = SkyActivityState.Stopped

                Lifecycle.Event.ON_DESTROY -> viewerRef.get()?.destroy()
                Lifecycle.Event.ON_PAUSE -> viewerRef.get()?.activityState = SkyActivityState.Paused

                Lifecycle.Event.ON_RESUME -> viewerRef.get()?.activityState =
                    SkyActivityState.Resumed

                else -> {
                    /*no-op*/
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiData.realFontSize) {
        viewerRef.get()?.setFontSizeIfNotLoading(uiData.realFontSize)
    }

    AndroidView(
        modifier = modifier,
        factory = { factoryContext ->
            Timber.v("epub log viewer factory, ${uiData.initialPositionInBook}")
            isInitLoading = true
            currentOnLoadingStateChange.invoke(true)
            SkyEpubReflowableControl(factoryContext, uiData.bookCode, uiData.realFontSize).apply {
                viewerRef = WeakReference(this)

                // init
                setBookPath(uiData.bookPath)
                setContentProvider(uiData.bookProvider)
                setStartPositionInBook(uiData.initialPositionInBook)

                // setListener, must call this to get totalPages by analysis global pagingInfo
                setScanListener(
                    scanFinishedListener = { max ->
                        currentOnGetTotalPagesChanged.invoke(max)
                        isInitLoading = false
                        currentOnLoadingStateChange.invoke(false)
                    },
                    getNavListener = {
                        onGetNavList.invoke(it)
                    }
                )
                setLoadingListener {
                    if (isInitLoading.not()) {
                        currentOnLoadingStateChange.invoke(it)
                    }
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
            requestJumpNav?.let {
                if (view.isPaging.not()) {
                    view.gotoPageByNavPoint(it)
                    currentOnRequestPageFinished.invoke()
                }
            }
        }
    )
}
