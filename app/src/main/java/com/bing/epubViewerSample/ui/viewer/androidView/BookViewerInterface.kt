package com.bing.epubViewerSample.ui.viewer.androidView

import com.bing.epubViewerSample.ui.viewer.BookViewerContract
import com.skytree.epub.NavPoint

interface BookViewerInterface {
    fun setLoadingListener(listener: (isLoading: Boolean) -> Unit)
    fun setOnPageMovedListener(listener: (BookViewerContract.BookPagingInfo) -> Unit)
    fun setOnScreenClicked(onScreenClicked: () -> Unit)

    fun setScanListener(
        listener: (totalPage: Int) -> Unit,
        getNavListener: (List<NavPoint>) -> Unit
    )
}