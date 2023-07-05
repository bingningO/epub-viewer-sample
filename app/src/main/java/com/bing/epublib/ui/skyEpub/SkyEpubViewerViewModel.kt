package com.bing.epublib.ui.skyEpub

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bing.epublib.epubDomain.EpubFileReader
import com.bing.epublib.ui.common.UIEventHandler
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.SkyEpubViewerEvent
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiData
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiInput
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiState
import com.skytree.epub.SkyProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkyEpubViewerViewModel @Inject constructor(
    private val epubFileReader: EpubFileReader
) : ViewModel(), SkyEpubViewerContract.ViewModel {

    private val eventHandler = UIEventHandler<SkyEpubViewerEvent>(viewModelScope)
    private var _uiData: UiData by mutableStateOf(UiData())
    override val uiState: UiState = object : UiState {
        override var uiData: UiData = _uiData
        override val events: List<SkyEpubViewerEvent> = eventHandler.eventState
    }

    override val uiInput: UiInput = object : UiInput {
        override val onEventConsumed = eventHandler.onEventConsumed
    }

    // todo make the books can be selected at the screen
    private val bookName = "Alice.epub"

    init {
        viewModelScope.launch {
            prepareData()
        }
    }

    private suspend fun prepareData() {
        _uiData = _uiData.copy(isLoading = true)
        try {
            epubFileReader.prepareBook(bookName)
        } catch (e: Throwable) {
            _uiData = _uiData.copy(error = e)
        } finally {
            _uiData = _uiData.copy(
                isLoading = false,
                bookProvider = SkyProvider(),
                bookPath = epubFileReader.getBookPath(bookName)
            )
        }
    }

}
