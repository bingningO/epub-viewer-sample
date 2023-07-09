package com.bing.epublib.ui.skyEpub

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.bing.epublib.ui.common.composable.ErrorScreen
import com.bing.epublib.ui.common.composable.LoadingScreen
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiData
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference

@Composable
fun SkyEpubViewerScreen(
    viewModel: SkyEpubViewerContract.ViewModel = hiltViewModel<SkyEpubViewerViewModel>()
) {
    val uiState = viewModel.uiState
    val uiInput = viewModel.uiInput

    uiState.events.firstOrNull()?.let { event ->
        LaunchedEffect(event.id) {
            // todo when(event)
            uiInput.onEventConsumed.emit(event)
        }
    }
    SkyEpubViewerContent(
        uiData = uiState.uiData,
        uiInput = uiInput
    )
}

@Composable
private fun SkyEpubViewerContent(
    uiData: UiData,
    uiInput: SkyEpubViewerContract.UiInput
) {
    when {
        uiData.isInitLoading -> {
            // loading for preparing book data
            LoadingScreen()
        }

        uiData.error != null -> {
            ErrorScreen(error = uiData.error!!)
        }

        else -> {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                SkyEpubViewerSuccessContent(
                    uiData = uiData,
                    uiInput = uiInput
                )

                // loading when SDK is analysis, todo want to show cover and progress line bar
                if (uiData.isAnalysisLoading) {
                    LoadingScreen()
                }
            }
        }
    }
}

@Composable
private fun SkyEpubViewerSuccessContent(
    uiData: UiData,
    uiInput: SkyEpubViewerContract.UiInput
) {
    val scope = rememberCoroutineScope()

    SkyEpubViewer(
        uiData = uiData,
        onLoadingStateChange = {
            scope.launch { uiInput.onLoadingStateChanged.emit(it) }
        }
    )
    // add controller ... toc ...
}

@Composable
private fun SkyEpubViewer(
    modifier: Modifier = Modifier,
    uiData: UiData,
    onLoadingStateChange: (Boolean) -> Unit
) {
    var viewerRef by remember {
        mutableStateOf(WeakReference<SkyEpubCustomViewer>(null))
    }
    var bookFilePath: String? by remember { mutableStateOf(null) }
    val currentOnLoadingStateChange by rememberUpdatedState(onLoadingStateChange)

    AndroidView(
        modifier = modifier,
        factory = { factoryContext ->
            Timber.v("epub log viewer factory")
            SkyEpubCustomViewer(factoryContext).apply {
                viewerRef = WeakReference(this)

                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                setLoadingListener {
                    currentOnLoadingStateChange.invoke(it)
                }
            }
        },
        update = { view ->
            Timber.v("epub log viewer update")
            // might need if below 
            if (bookFilePath != uiData.bookPath) {
                bookFilePath = uiData.bookPath
                view.setBookPath(uiData.bookPath)
                view.isRotationLocked = true
                view.setStartPositionInBook(0f)
                view.setContentProvider(uiData.bookProvider)
            }
        }
    )

}