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
import com.bing.epubViewerSample.ui.skyEpub.SkyEpubViewerContract.BookPagingInfo
import com.skytree.epub.NavPoint
import com.skytree.epub.SkyActivityState
import timber.log.Timber
import java.lang.ref.WeakReference

@Composable
internal fun SkyEpubViewer(
    modifier: Modifier = Modifier,
    uiData: SkyEpubViewerContract.UiData,
    onLoadingStateChange: (Boolean) -> Unit,
    onPageChanged: (BookPagingInfo) -> Unit,
    skyEpubViewerUiState: SkyEpubViewerUiState<NavPoint>,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var viewerRef by remember {
        mutableStateOf(WeakReference<SkyEpubReflowableControl>(null))
    }
    var isInitLoading by remember { mutableStateOf(false) }

    // always read the updated value if called inside AndroidView#factory
    val currentOnLoadingStateChange by rememberUpdatedState(onLoadingStateChange)
    val currentOnPageChanged by rememberUpdatedState(newValue = onPageChanged)
    val currentUiState by rememberUpdatedState(newValue = skyEpubViewerUiState)

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
                    scanFinishedListener = { max, currentIndex ->
                        currentUiState.seekBarState.onPageInfoChanged(
                            currentIndex = currentIndex,
                            totalPage = max
                        )
                        isInitLoading = false
                        currentOnLoadingStateChange.invoke(false)
                    },
                    getNavListener = {
                        currentUiState.bookIndexState.onIndexDataInitialized(it)
                    }
                )
                setLoadingListener {
                    if (isInitLoading.not()) {
                        currentOnLoadingStateChange.invoke(it)
                    }
                }
                setOnPageMovedListener { info ->
                    currentOnPageChanged.invoke(info)
                }
                setOnScreenClicked {
                    currentUiState.updateShowTopContent(true)
                }

            }
        },
        update = { view ->
            Timber.v("epub log viewer update")
            /**
             * todo
             * how about MAD's implementation of LaunchedEffect?
             * LaunchedEffect(onProgressChangeRequest) {
             *     ...
             * }
             */
            skyEpubViewerUiState.seekBarState.onProgressChangeRequest?.let {
                if (view.isPaging.not()) {
                    val ppb = view.getPagePositionInBookByPageIndexInBook(it)
                    // So absolute position in epub is expressed as pagePositionInBook.
                    // This is float value from 0.0f to 1.0f for entire book.
                    view.gotoPageByPagePositionInBook(ppb)

                    skyEpubViewerUiState.seekBarState.onProgressChangeRequestConsumed()
                }
            }
            skyEpubViewerUiState.bookIndexState.onSelectedIndex?.let {
                if (view.isPaging.not()) {
                    view.gotoPageByNavPoint(it)
                    skyEpubViewerUiState.bookIndexState.onIndexJumpConsumed()
                }
            }
        },
    )
}
