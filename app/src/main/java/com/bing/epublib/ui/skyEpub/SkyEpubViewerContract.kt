package com.bing.epublib.ui.skyEpub

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.bing.epublib.ui.common.UIEvent
import com.skytree.epub.SkyProvider
import kotlinx.coroutines.flow.FlowCollector

class SkyEpubViewerContract {

    interface ViewModel {
        val uiState: UiState
        val uiInput: UiInput
    }

    interface UiState {
        val uiData: UiData
        val events: List<SkyEpubViewerEvent>
    }

    interface UiInput {
        val onEventConsumed: FlowCollector<SkyEpubViewerEvent>
    }

    @Stable
    data class UiData(
        val isLoading: Boolean = true,
        val error: Throwable? = null,
        val bookProvider: SkyProvider? = null,
        val bookPath: String = ""
    )

    @Immutable
    sealed class SkyEpubViewerEvent : UIEvent() {

    }
}