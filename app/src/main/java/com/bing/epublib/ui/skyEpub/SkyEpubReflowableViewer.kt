package com.bing.epublib.ui.skyEpub

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.BookMetaData
import com.skytree.epub.ClickListener
import com.skytree.epub.ItemRef
import com.skytree.epub.PageInformation
import com.skytree.epub.PageMovedListener
import com.skytree.epub.PageTransition
import com.skytree.epub.PagingInformation
import com.skytree.epub.PagingListener
import com.skytree.epub.ReflowableControl
import com.skytree.epub.State
import timber.log.Timber

/**
 * custom viewer extending SkyEpub SDK#ReflowableControl
 */
class SkyEpubReflowableViewer(context: Context) : ReflowableControl(context) {

    init {
        setForegroundColor(Color.White.toArgb())
        setBackgroundColor(Color.Gray.toArgb())
        setPageTransition(PageTransition.Curl)

        // if true, globalPagination will be activated.
        // this enables the calculation of page number based on entire book ,not on each chapter.
        // this globalPagination consumes huge computing power.
        // AVOID GLOBAL PAGINATION FOR LOW SPEC DEVICES.
        // set it as true for getting page number of entire book.
        isGlobalPagination = true
    }

    // for display in other composables
    fun getBookMetaData(): BookMetaData {
        val data = book
        return BookMetaData(
            title = data.title,
            description = data.description,
            isFixedLayout = data.isFixedLayout,
            orientation = data.orientation
        )
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

    /**
     * @param listener pageIndex: Int start from 0 ~ maxIndex
     */
    fun setOnPageMovedListener(listener: (currentIndex: Int, maxIndex: Int) -> Unit) {
        setPageMovedListener(object : PageMovedListener {

            override fun onPageMoved(pi: PageInformation?) {
                // see sample app#processPageMoved
                Timber.v("epub log onPageMoved: ${pi?.pageIndex}, ${pi?.pageIndexInBook}, ${pi?.numberOfPagesInBook}, ${pi?.numberOfPagesInChapter} ${pi?.endIndex}")
                listener.invoke(pi?.pageIndexInBook ?: 0, (pi?.numberOfPagesInBook ?: 1) - 1)
            }

            /** called when new chapter is loaded.  */
            override fun onChapterLoaded(chapterIndex: Int) {
                // do nothing
            }

            override fun onFailedToMove(isFirstPage: Boolean) {
                if (isFirstPage) {
                    showToast("This is the first page.")
                } else {
                    showToast("This is the last page.")
                }
            }
        })
    }

    fun setOnScreenClicked(onScreenClicked: () -> Unit) {
        setClickListener(object : ClickListener {
            /** called when screen is clicked  */
            override fun onClick(p0: Int, p1: Int) {
                onScreenClicked.invoke()
            }

            override fun onImageClicked(p0: Int, p1: Int, p2: String?) {
                // do nothing
            }

            override fun onLinkClicked(p0: Int, p1: Int, p2: String?) {
                // do nothing
            }

            override fun onLinkForLinearNoClicked(p0: Int, p1: Int, p2: String?) {
                // do nothing
            }

            override fun ignoreLink(p0: Int, p1: Int, p2: String?): Boolean {
                return false
            }

            override fun onIFrameClicked(p0: Int, p1: Int, p2: String?) {
                // do nothing
            }

            override fun onVideoClicked(p0: Int, p1: Int, p2: String?) {
                // do nothing
            }

            override fun onAudioClicked(p0: Int, p1: Int, p2: String?) {
                // do nothing
            }
        })
    }


    fun setTotalPagesListener(listener: (totalPage: Int) -> Unit) {
        // set the pagingListener which is called when GlobalPagination is true.
        // this enables the calculation for the total number of pages in book, not in chapter.
        setPagingListener(object : PagingListener {
            override fun onPagingStarted(p0: Int) {
                Timber.v("epub log onPagingStarted: $p0")
            }

            override fun onPaged(p0: PagingInformation?) {
                Timber.v("epub log onPaged: ${p0?.numberOfPagesInChapter}")
            }

            override fun onPagingFinished(p0: Int) {
                Timber.v("epub log onPagingFinished: $p0, $numberOfPagesInBook")
            }

            override fun getNumberOfPagesForPagingInformation(p0: PagingInformation?): Int {
                Timber.v("epub log getNumberOfPagesForPagingInformation: ${p0?.numberOfPagesInChapter}")
                return p0?.numberOfPagesInChapter ?: 0
            }

            override fun onScanStarted(p0: Int) {
                Timber.v("epub log onScanStarted: $p0")
            }

            override fun onScanned(p0: ItemRef?) {
                Timber.v("epub log onScanned: ${p0?.pagingInformation?.numberOfPagesInChapter}")
            }

            override fun onScanFinished(p0: Int) {
                Timber.v("epub log onScanFinished: $p0, $numberOfPagesInBook")
                listener.invoke(numberOfPagesInBook)
            }

            override fun onTextExtracted(p0: Int, p1: Int, p2: String?) {
                Timber.v("epub log onTextExtracted: $p0, $p1, $p2")
            }

            override fun getText(p0: Int, p1: Int): String {
                Timber.v("epub log getText: $p0, $p1")
                return ""
            }

            override fun getAnyPagingInformations(p0: Int, p1: Int): ArrayList<*> {
                Timber.v("epub log getAnyPagingInformations: $p0, $p1")
                return arrayListOf<Any>()
            }

            override fun getPagingInformation(p0: PagingInformation?): PagingInformation {
                Timber.v("epub log getPagingInformation: ${p0?.numberOfPagesInChapter}")
                return p0 ?: PagingInformation()
            }

        })
    }
}