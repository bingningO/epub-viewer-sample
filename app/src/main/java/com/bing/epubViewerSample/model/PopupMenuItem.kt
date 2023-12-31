package com.bing.epubViewerSample.model

import androidx.compose.runtime.Stable
import com.bing.epubViewerSample.R

@Stable
interface PopupMenuItem {
    val textResId: Int
}

enum class FontSize : PopupMenuItem {
    BIGGER {
        override val textResId: Int
            get() = R.string.font_size_bigger
    },
    SMALLER {
        override val textResId: Int
            get() = R.string.font_size_smaller
    },
}
