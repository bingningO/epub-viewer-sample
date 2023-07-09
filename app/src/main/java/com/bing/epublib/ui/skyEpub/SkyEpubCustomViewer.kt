package com.bing.epublib.ui.skyEpub

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.skytree.epub.PageTransition
import com.skytree.epub.ReflowableControl
import com.skytree.epub.State
import timber.log.Timber

/**
 * custom viewer extending SkyEpub SDK#ReflowableControl
 */
class SkyEpubCustomViewer(context: Context) : ReflowableControl(context) {

    init {
        setForegroundColor(Color.White.toArgb())
        setBackgroundColor(Color.Gray.toArgb())
        setPageTransition(PageTransition.Curl)
    }

    fun setLoadingListener(listener: (isLoading: Boolean) -> Unit) {
        Timber.v("epub log setLoadingListner init")
        setStateListener { state ->
            Timber.v("epub log state: $state")
            val isLoading = when (state) {
                State.LOADING -> true
                else -> false
            }
            listener.invoke(isLoading)
        }
    }
}