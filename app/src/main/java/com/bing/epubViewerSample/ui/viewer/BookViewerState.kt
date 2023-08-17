package com.bing.epubViewerSample.ui.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.skytree.epub.NavPoint

@Stable
class BookViewerState<T>(
    val seekBarState: SeekBarState,
    val bookIndexState: BookIndexState<T>
) {
    var isShowTopContent by mutableStateOf(false)
        private set
    var isTOCVisible by mutableStateOf(false)
        private set

    var currentIndex by mutableStateOf(0)
        private set
    var totalPage by mutableStateOf(0)
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

    fun onPageInfoChanged(currentIndex: Int, totalPage: Int) {
        this.currentIndex = currentIndex
        this.totalPage = totalPage
    }
}

@Composable
// todo add [Saver] for all state
fun rememberBookViewerState(
    seeksBarState: SeekBarState = rememberSeekBarState(),
    bookIndexState: BookIndexState<NavPoint> = rememberBookIndexState()
) = remember(
    seeksBarState,
    bookIndexState,
) {
    BookViewerState(
        seekBarState = seeksBarState,
        bookIndexState = bookIndexState
    )
}

@Stable
class SeekBarState {
    var onProgressChangeRequest: Int? by mutableStateOf(null)
        private set

    fun onProgressChangeRequest(index: Int) {
        onProgressChangeRequest = index
    }

    fun onProgressChangeRequestConsumed() {
        onProgressChangeRequest = null
    }

}

@Composable
fun rememberSeekBarState() = remember { SeekBarState() }

@Stable
class BookIndexState<T> {

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

    fun onIndexJumpConsumed() {
        onSelectedIndex = null
    }
}

@Composable
fun <T> rememberBookIndexState() = remember { BookIndexState<T>() }
