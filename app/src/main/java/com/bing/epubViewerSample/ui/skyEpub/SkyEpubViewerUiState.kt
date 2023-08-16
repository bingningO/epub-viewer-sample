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
        bookIndexState.onIndexClicked(index)
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
    private var _currentIndex by mutableStateOf(0)
    private var _totalPage by mutableStateOf(0)

    val totalPage: Int
        get() = _totalPage
    val currentIndex: Int
        get() = _currentIndex

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
        this._currentIndex = currentIndex
        this._totalPage = totalPage
    }
}

@Composable
fun rememberSeekBarState() = remember { SeekBarState() }

@Stable
class BookIndexState<T> {
    private var _indexList by mutableStateOf(listOf<ViewerIndexData<T>>())
    private var _onSelectedIndex by mutableStateOf<T?>(null)
    val indexList: List<ViewerIndexData<T>>
        get() = _indexList
    val onSelectedIndex: T?
        get() = _onSelectedIndex

    fun onIndexClicked(index: T) {
        _onSelectedIndex = index
    }

    fun onIndexDataInitialized(index: List<ViewerIndexData<T>>) {
        _indexList = index
    }

    fun onIndexJumpConsumed() {
        _onSelectedIndex = null
    }
}

@Composable
fun <T> rememberBookIndexState() = remember { BookIndexState<T>() }
