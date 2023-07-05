package com.bing.epublib.ui.skyEpub

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.skytree.epub.PageTransition
import com.skytree.epub.ReflowableControl

/**
 * custom viewer extending SkyEpub SDK#ReflowableControl
 */
class SkyEpubCustomViewer(context: Context) : ReflowableControl(context) {

    init {
        setForegroundColor(Color.White.toArgb())
        setBackgroundColor(Color.Gray.toArgb())
        setPageTransition(PageTransition.Curl)
    }
}