package com.bing.epubViewerSample.ui.skyEpub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bing.epubViewerSample.ui.common.viewer.ViewerIndexData

// todo does it need Saver to survival activity recreation?
@Stable
class SkyEpubViewerUiState<T>(
    val seekBarState: SeekBarState,
    val bookIndexState: BookIndexState<T>
) {
    var isShowTopContent by mutableStateOf(false)
        private set
    var isTOCVisible by mutableStateOf(false)
        private set

    fun onBookIndexClicked(index: T) {
        bookIndexState.onIndexClicked(index)
        isTOCVisible = false
    }

    fun setTocVisible(visible: Boolean) {
        isTOCVisible = visible
    }

    fun setShowTopContent(visible: Boolean) {
        isShowTopContent = visible
    }
}

@Composable
fun <T> rememberSkyEpubViewerUiState(
    seeksBarState: SeekBarState = rememberSeekBarState(),
    bookIndexState: BookIndexState<T> = rememberBookIndexState()
) = remember(
    seeksBarState,
    bookIndexState,
) {
    SkyEpubViewerUiState<T>(
        seekBarState = seeksBarState,
        bookIndexState = bookIndexState
    )
}

@Stable
class SeekBarState {
    var currentIndex by mutableStateOf(0)
        private set
    var totalPage by mutableStateOf(0)
        private set

    private var _onProgressChangeRequest: Int? by mutableStateOf(null)
    val onProgressChangeRequest: Int?
        get() = _onProgressChangeRequest

    fun onProgressChangeRequest(index: Int) {
        _onProgressChangeRequest = index
    }

    fun onProgressChangeRequestConsumed() {
        _onProgressChangeRequest = null
    }

    fun onPageInfoChanged(currentIndex: Int, totalPage: Int) {
        this.currentIndex = currentIndex
        this.totalPage = totalPage
    }
}

@Composable
fun rememberSeekBarState() = remember { SeekBarState() }

@Stable
class BookIndexState<T> {
    var indexList by mutableStateOf(listOf<ViewerIndexData<T>>())
        private set
    var onSelectedIndex by mutableStateOf<T?>(null)
        private set

    fun onIndexClicked(index: T) {
        onSelectedIndex = index
    }

    fun onIndexDataInitialized(index: List<ViewerIndexData<T>>) {
        indexList = index
    }

    fun onIndexJumpConsumed() {
        onSelectedIndex = null
    }
}

@Composable
fun <T> rememberBookIndexState() = remember { BookIndexState<T>() }
