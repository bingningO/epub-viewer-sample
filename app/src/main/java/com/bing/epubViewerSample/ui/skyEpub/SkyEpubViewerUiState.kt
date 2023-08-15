package com.bing.epubViewerSample.ui.skyEpub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bing.epubViewerSample.ui.common.viewer.ViewerIndexData

@Stable
class SkyEpubViewerUiState<T>(
    val seekBarState: SeekBarState,
    val bookIndexState: BookIndexState<T>
) {
    private var _isShowTopContent by mutableStateOf(false)
    private var _isTOCVisible by mutableStateOf(false)
    val isShowTopContent: Boolean
        get() = _isShowTopContent
    val isTOCVisible: Boolean
        get() = _isTOCVisible

    fun onBookIndexClicked(index: T) {
        bookIndexState.onSelectedIndex = index
        _isTOCVisible = false
    }

    fun setTocVisible(visible: Boolean) {
        _isTOCVisible = visible
    }

    fun setShowTopContent(visible: Boolean) {
        _isShowTopContent = visible
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
    var totalPage by mutableStateOf(0)

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
    var onSelectedIndex by mutableStateOf<T?>(null)

    fun onIndexDataInitialized(index: List<ViewerIndexData<T>>) {
        indexList = index
    }

    fun onIndexJumpConsumed() {
        onSelectedIndex = null
    }
}

@Composable
fun <T> rememberBookIndexState() = remember { BookIndexState<T>() }
