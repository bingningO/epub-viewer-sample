package com.bing.epublib.ui.skyEpub

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SkyEpubViewerViewModel @Inject constructor(
    private val epubFileReader: EpubFileReader
) : ViewModel(), SkyEpubViewerContract.ViewModel {

    private val eventHandler = UIEventHandler<SkyEpubViewerEvent>(viewModelScope)
    private val _uiData = MutableStateFlow(UiData())
    override val uiState: UiState = object : UiState {
        override var uiData: StateFlow<UiData> = _uiData.asStateFlow()
        override val events: List<SkyEpubViewerEvent> = eventHandler.eventState
    }

    override val uiInput: UiInput = object : UiInput {
        override val onEventConsumed = eventHandler.onEventConsumed
    }

    // todo make the books can be selected at the screen
    private val bookName = "sample1.epub"

    init {
        viewModelScope.launch {
            prepareData()
        }
    }

    private suspend fun prepareData() {
        _uiData.update { it.copy(isLoading = true) }
        Timber.v("epub start ${_uiData.value.isLoading}")
        try {
            epubFileReader.prepareBook(bookName)
        } catch (e: Throwable) {
            _uiData.update { it.copy(error = e) }
        } finally {
            _uiData.update {
                it.copy(
                    isLoading = false,
                    bookProvider = SkyProvider(),
                    bookPath = epubFileReader.getBookPath(bookName)
                )
            }
            Timber.v("epub end ${_uiData.value.isLoading}, path: ${_uiData.value.bookPath}")
        }
    }

}
