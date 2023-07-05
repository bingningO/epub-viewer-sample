package com.bing.epublib.ui.skyEpub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bing.epublib.ui.common.composable.ErrorScreen
import com.bing.epublib.ui.common.composable.LoadingScreen
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiData
import com.skytree.epub.ReflowableControl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@Composable
fun SkyEpubViewerScreen(
    viewModel: SkyEpubViewerViewModel
) {
    val uiData = viewModel.uiState.uiData
    when {
        uiData.isLoading -> {
            LoadingScreen()
        }

        uiData.error != null -> {
            ErrorScreen(error = uiData.error)
        }

        else -> {
            SkyEpubViewerSuccessContent(
                uiState = viewModel.uiState,
                uiInput = viewModel.uiInput
            )
        }
    }
}

@Composable
private fun SkyEpubViewerSuccessContent(
    uiState: SkyEpubViewerContract.UiState,
    uiInput: SkyEpubViewerContract.UiInput
) {
    SkyEpubViewerContent(uiData = uiState.uiData)
}

@Composable
private fun SkyEpubViewerContent(
    modifier: Modifier = Modifier,
    uiData: UiData
) {
    val scope = rememberCoroutineScope()
    var viewerRef by remember {
        mutableStateOf(WeakReference<ReflowableControl>(null))
    }
    var bookFilePath: String? by remember { mutableStateOf(null) }

    AndroidView(
        modifier = modifier,
        factory = { factoryContext ->
            ReflowableControl(factoryContext).apply {
                viewerRef = WeakReference(this)
                // todo other init setting like listeners
            }
        },
        update = { view ->
            // might need if below 
            if (bookFilePath != uiData.bookPath) {
                bookFilePath = uiData.bookPath
                view.setBookPath(uiData.bookPath)
                view.isRotationLocked = true
                scope.launch {
                    delay(100)
                    view.setContentProvider(uiData.bookProvider)
                }
            }
        }
    )

}