package com.bing.epubViewerSample.ui.skyEpub

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bing.epubViewerSample.epubDomain.EpubFileHandler
import com.bing.epubViewerSample.epubDomain.EpubFileReader
import com.bing.epubViewerSample.model.EpubInfo
import com.bing.epubViewerSample.repository.EpubInfoRepository
import com.bing.epubViewerSample.ui.common.UIEventHandler
import com.bing.epubViewerSample.ui.skyEpub.SkyEpubViewerContract.MutableUiData
import com.bing.epubViewerSample.ui.skyEpub.SkyEpubViewerContract.SkyEpubViewerEvent
import com.bing.epubViewerSample.ui.skyEpub.SkyEpubViewerContract.UiData
import com.bing.epubViewerSample.ui.skyEpub.SkyEpubViewerContract.UiInput
import com.bing.epubViewerSample.ui.skyEpub.SkyEpubViewerContract.UiState
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
    private val _uiData = MutableUiData()
    override val uiState: UiState = object : UiState {
        override val uiData: UiData = _uiData
        override val events: List<SkyEpubViewerEvent> by eventHandler.eventState
    }

    private val _onLoadingStateChanged = MutableSharedFlow<Boolean>()
    private val _onChangePagePosition = MutableSharedFlow<Double>()
    private val _onClickFontSizeBigger = MutableSharedFlow<Unit>()
    private val _onClickFontSizeSmaller = MutableSharedFlow<Unit>()

    override val uiInput: UiInput = object : UiInput {
        override val onEventConsumed = eventHandler.onEventConsumed
        override val onLoadingStateChanged = _onLoadingStateChanged
        override val onChangePagePosition = _onChangePagePosition
        override val onClickFontSizeBigger = _onClickFontSizeBigger
        override val onClickFontSizeSmaller = _onClickFontSizeSmaller
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

        _onClickFontSizeSmaller.onEach {
            setFontSize(_uiData.fontSizeIndex - 1)
        }.launchIn(viewModelScope)
        _onClickFontSizeBigger.onEach {
            setFontSize(_uiData.fontSizeIndex + 1)
        }.launchIn(viewModelScope)
    }

    private fun setFontSize(fontSizeIndexToChange: Int) {
        var rs = when (fontSizeIndexToChange) {
            -5 -> {
                eventHandler.scheduleEvent(SkyEpubViewerEvent.ShowToast("Already the smallest font size"))
                return
            }

            -4 -> 10
            -3 -> 14
            -2 -> 17
            -1 -> 20

            0 -> 24
            1 -> 27
            2 -> 30
            3 -> 34
            4 -> 37
            else -> {
                eventHandler.scheduleEvent(SkyEpubViewerEvent.ShowToast("Already the smallest font size"))
                return
            }
        }
        rs = (rs.toDouble() * 0.75f).toInt()
        if (fontSizeIndexToChange in -4..4) {
            _uiData.fontSizeIndex = fontSizeIndexToChange
            _uiData.realFontSize = rs
        }
        Timber.v("epub log setFontSize: ${_uiData.realFontSize}, ${_uiData.fontSizeIndex}")
    }
}
