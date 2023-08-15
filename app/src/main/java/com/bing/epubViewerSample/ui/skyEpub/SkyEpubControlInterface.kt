package com.bing.epubViewerSample.ui.skyEpub

import com.bing.epubViewerSample.ui.common.viewer.ViewerIndexData
import com.skytree.epub.NavPoint

interface SkyEpubControlInterface {
    fun setLoadingListener(listener: (isLoading: Boolean) -> Unit)
    fun setOnPageMovedListener(listener: (SkyEpubViewerContract.BookPagingInfo) -> Unit)
    fun setOnScreenClicked(onScreenClicked: () -> Unit)

    fun setScanListener(
        listener: (totalPage: Int) -> Unit,
        getNavListener: (List<ViewerIndexData<NavPoint>>) -> Unit
    )
}