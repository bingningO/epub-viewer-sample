package com.bing.epublib.ui.skyEpub

import com.bing.epublib.ui.common.viewer.ViewerIndexData
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