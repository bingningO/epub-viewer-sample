package com.bing.epublib.ui.skyEpub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.bing.epublib.ui.common.composable.ErrorScreen
import com.bing.epublib.ui.common.composable.LoadingScreen
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiData
import timber.log.Timber
import java.lang.ref.WeakReference

@Composable
fun SkyEpubViewerScreen(
    viewModel: SkyEpubViewerContract.ViewModel = hiltViewModel<SkyEpubViewerViewModel>()
) {
    val uiState = viewModel.uiState
    val uiInput = viewModel.uiInput
    val uiData = uiState.uiData.collectAsState().value

    uiState.events.firstOrNull()?.let { event ->
        LaunchedEffect(event.id) {
            // todo when(event)
            uiInput.onEventConsumed.emit(event)
        }
    }
    SkyEpubViewerContent(
        uiData = uiData,
        uiInput = uiInput
    )
}

@Composable
private fun SkyEpubViewerContent(
    uiData: UiData,
    uiInput: SkyEpubViewerContract.UiInput
) {
    Timber.v("SkyEpubViewerContent build loading: ${uiData.isLoading}, error: ${uiData.error}")
    when {
        uiData.isLoading -> {
            LoadingScreen()
        }

        uiData.error != null -> {
            ErrorScreen(error = uiData.error)
        }

        else -> {
            SkyEpubViewerSuccessContent(
                uiData = uiData
            )
        }
    }
}

@Composable
private fun SkyEpubViewerSuccessContent(
    uiData: UiData,
) {
    SkyEpubViewer(uiData = uiData)
    // add controller ... toc ...
}

@Composable
private fun SkyEpubViewer(
    modifier: Modifier = Modifier,
    uiData: UiData
) {
    val scope = rememberCoroutineScope()
    var viewerRef by remember {
        mutableStateOf(WeakReference<SkyEpubCustomViewer>(null))
    }
    var bookFilePath: String? by remember { mutableStateOf(null) }

    AndroidView(
        modifier = modifier,
        factory = { factoryContext ->
            SkyEpubCustomViewer(factoryContext).apply {
                viewerRef = WeakReference(this)
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                // todo other init setting like listeners
            }
        },
        update = { view ->
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