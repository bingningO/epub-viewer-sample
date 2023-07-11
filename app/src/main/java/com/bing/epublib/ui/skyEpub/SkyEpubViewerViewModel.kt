@file:OptIn(FlowPreview::class)

package com.bing.epublib.ui.skyEpub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bing.epublib.epubDomain.EpubFileReader
import com.bing.epublib.ui.common.UIEventHandler
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.SkyEpubViewerEvent
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.StableBookProvider
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiData
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiInput
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SkyEpubViewerViewModel @Inject constructor(
    private val epubFileReader: EpubFileReader
) : ViewModel(), SkyEpubViewerContract.ViewModel {

    private val eventHandler = UIEventHandler<SkyEpubViewerEvent>(viewModelScope)
    private val _uiData = SkyEpubViewerContract.MutableUiData()
    override val uiState: UiState = object : UiState {
        override val uiData: UiData = _uiData
        override val events: List<SkyEpubViewerEvent> = eventHandler.eventState
    }

    private val _onLoadingStateChanged = MutableSharedFlow<Boolean>()

    override val uiInput: UiInput = object : UiInput {
        override val onEventConsumed = eventHandler.onEventConsumed
        override val onLoadingStateChanged = _onLoadingStateChanged
    }

    // todo make the books can be selected at the screen
    private val bookName = "Alice.epub"
    private var isPreparingData = true

    init {
        startObserveUiInput()

        viewModelScope.launch {
            prepareData()
        }
    }

    private suspend fun prepareData() {
        isPreparingData = true
        try {
            epubFileReader.prepareBook(bookName)
        } catch (e: Throwable) {
            Timber.e(e, "epub prepare book error")
            _uiData.error = e
        } finally {
            isPreparingData = false
            _uiData.bookProvider = StableBookProvider()
            _uiData.bookPath = epubFileReader.getBookPath(bookName)
        }
    }

    private fun startObserveUiInput() {
        _onLoadingStateChanged.onEach { isLoading ->
            _uiData.isLoading = isLoading || isPreparingData
        }.launchIn(viewModelScope)
    }
}
