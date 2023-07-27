package com.bing.epublib.ui.skyEpub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bing.epublib.epubDomain.EpubFileHandler
import com.bing.epublib.epubDomain.EpubFileReader
import com.bing.epublib.model.EpubInfo
import com.bing.epublib.repository.EpubInfoRepository
import com.bing.epublib.ui.common.UIEventHandler
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.SkyEpubViewerEvent
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiData
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiInput
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.UiState
import com.skytree.epub.SkyProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SkyEpubViewerViewModel @Inject constructor(
    private val epubFileHandler: EpubFileHandler,
    private val dataRepository: EpubInfoRepository,
    private val epubFileReaderFactory: EpubFileReader.Factory,
) : ViewModel(), SkyEpubViewerContract.ViewModel {

    private val eventHandler = UIEventHandler<SkyEpubViewerEvent>(viewModelScope)
    private val _uiData = SkyEpubViewerContract.MutableUiData()
    override val uiState: UiState = object : UiState {
        override val uiData: UiData = _uiData
        override val events: List<SkyEpubViewerEvent> = eventHandler.eventState
    }

    private val _onLoadingStateChanged = MutableSharedFlow<Boolean>()
    private val _onChangePagePosition = MutableSharedFlow<Double>()

    override val uiInput: UiInput = object : UiInput {
        override val onEventConsumed = eventHandler.onEventConsumed
        override val onLoadingStateChanged = _onLoadingStateChanged
        override val onChangePagePosition = _onChangePagePosition
    }

    // change the book name here to open different book file(also they need to be copied to assets dir)
    private val bookName = "Alice.epub"
//    private val bookName = "page-blanche-fixed.epub"

    private val bookFileCode = 1
    private var isPreparingData = true
    private var currentPositionInBook = 0.0
    private val epubFileReader = epubFileReaderFactory.create(bookName)

    init {
        startObserveUiInput()

        viewModelScope.launch {
            prepareData()
        }
    }

    override fun onCleared() {
        epubFileReader.onClose()
        epubFileHandler.onClose()
        super.onCleared()
    }

    private suspend fun prepareData() {
        isPreparingData = true

        dataRepository.getEpubInfo(bookFileCode).apply {
            val startPosition = this?.startPositionInBook ?: 0.0
            Timber.v("epub log startPosition: $startPosition")
            _uiData.initialPositionInBook = startPosition
            currentPositionInBook = startPosition
        }

        try {
            epubFileHandler.prepareBook(bookName)

            epubFileReader.isFixedLayout().let {
                Timber.v("epub log isFixedLayout: $it")
                // todo if it's a fixedLayout, open the fixedViewerScreen
                _uiData.isFixedLayout = it
            }
        } catch (e: Throwable) {
            Timber.e(e, "epub prepare book error")
            _uiData.error = e
        } finally {
            isPreparingData = false
            _uiData.bookProvider = SkyProvider()
            _uiData.bookPath = epubFileHandler.getBookPath(bookName)
            _uiData.bookCode = bookFileCode
        }
    }

    private fun startObserveUiInput() {
        _onLoadingStateChanged.onEach { isLoading ->
            _uiData.isLoading = isLoading || isPreparingData
        }.launchIn(viewModelScope)

        _onChangePagePosition.onEach {
            currentPositionInBook = it
            viewModelScope.launch {
                dataRepository.insertEpubInfo(
                    EpubInfo(
                        fileCode = bookFileCode,
                        startPositionInBook = currentPositionInBook
                    )
                )
            }
        }.launchIn(viewModelScope)
    }
}
