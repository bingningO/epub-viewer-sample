package com.bing.epubViewerSample.ui.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bing.epubViewerSample.ui.common.viewer.ViewerIndexData

@Stable
class BookViewerUiState<T>(
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

    fun updateTocVisible(visible: Boolean) {
        isTOCVisible = visible
    }

    fun updateShowTopContent(visible: Boolean) {
        isShowTopContent = visible
    }
}

@Composable
// todo no need for common T
// delete Ui
// todo add [Saver] for all state
fun <T> rememberSkyEpubViewerUiState(
    seeksBarState: SeekBarState = rememberSeekBarState(),
    bookIndexState: BookIndexState<T> = rememberBookIndexState()
) = remember(
    seeksBarState,
    bookIndexState,
) {
    BookViewerUiState<T>(
        seekBarState = seeksBarState,
        bookIndexState = bookIndexState
    )
}

@Stable
class SeekBarState {
    // todo move currentIndex&totalPage to skyEpubViewerState
    // -> how to sync currentIndex & Epub SDK#currentPage
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
    // ViewerIndexData<NavPoint> -> not good to rememberSavable with NavPoint
    // todo put this into viewModel, survival from configuration change
    // ネタ：if get data from view, where to put the state ( UI composable(savable-> bundle) or ViewModel )
    var indexList by mutableStateOf(listOf<ViewerIndexData<T>>())
        private set

    // todo shouldn't take it as a state -> UIInput
    var onSelectedIndex by mutableStateOf<T?>(null)
        private set

    // todo !! try to create event handle in Composable -> refer ViewModel#EventHandler
    // then also could get rid of LaunchEffect
    // 1. create uiEvent in ViewModel -> also explain MAD's EventHandler
    // 2. create uiEvent in Composable
    // do a comparison
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
