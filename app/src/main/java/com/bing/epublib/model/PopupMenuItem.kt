package com.bing.epublib.model

import androidx.compose.runtime.Stable
import com.bing.epublib.R

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
