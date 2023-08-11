package com.bing.epublib.ui.skyEpub

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.bing.epublib.ui.common.viewer.ViewerIndexData
import com.bing.epublib.ui.skyEpub.SkyEpubViewerContract.BookPagingInfo
import com.skytree.epub.ClickListener
import com.skytree.epub.ItemRef
import com.skytree.epub.NavPoint
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
class SkyEpubReflowableControl(context: Context, bookCode: Int, fontSize: Int) :
    ReflowableControl(context) {

    private val savedPagingInformation = arrayListOf<PagingInformation>()
    private var currentState = State.NORMAL

    init {
        setForegroundColor(Color.White.toArgb())
        setBackgroundColor(Color.Gray.toArgb())
        setPageTransition(PageTransition.Curl)
        setDoublePagedForLandscape(true)
        setSwipeEnabled(true)
        adjustContentWidth(true)
        // set the bookCode to identify the book file.
        setBookCode(bookCode)
        setFont(FONT_SIZE_NAME, fontSize)

        // if true, globalPagination will be activated.
        // this enables the calculation of page number based on entire book ,not on each chapter.
        // this globalPagination consumes huge computing power.
        // AVOID GLOBAL PAGINATION FOR LOW SPEC DEVICES.
        // set it as true for getting page number of entire book.
        isGlobalPagination = true
    }

    fun setLoadingListener(listener: (isLoading: Boolean) -> Unit) {
        setStateListener { state ->
            Timber.v("epub log viewer state: $state")
            currentState = state
            val isLoading = when (state) {
                State.LOADING, State.BUSY -> true
                else -> false
            }
            listener.invoke(isLoading)
        }
    }

    fun setFontSizeIfNotLoading(realFontSize: Int) {
        if (currentState != State.NORMAL) return
        changeFont(FONT_SIZE_NAME, realFontSize)
    }

    private fun getNavData(): List<ViewerIndexData<NavPoint>> {
        val navDatas = navPoints
        val navList = mutableListOf<ViewerIndexData<NavPoint>>()
        Timber.v("epub log get navPoints: ${navDatas.size}")
        (0 until navDatas.size).forEach {
            val nav = navDatas.getNavPoint(it)
            navList.add(
                ViewerIndexData(
                    indexTitle = nav.text,
                    nestLevel = nav.depth,
                    pageData = nav
                )
            )
        }
        return navList
    }

    fun setOnPageMovedListener(listener: (BookPagingInfo) -> Unit) {
        setPageMovedListener(object : PageMovedListener {

            override fun onPageMoved(pi: PageInformation?) {
                // be notice [totalPage] is changed during pagination
                if (isPaging.not()) {
                    listener.invoke(
                        BookPagingInfo(
                            totalPageInChapter = pi?.numberOfPagesInChapter ?: 0,
                            currentIndexInChapter = pi?.pageIndex ?: 0,
                            currentIndexInBook = pi?.pageIndexInBook ?: 0,
                            currentPositionInBook = pi?.pagePositionInBook ?: 0.0,
                            currentChapterIndex = pi?.chapterIndex ?: 0,
                            totalNumberOfChapters = pi?.numberOfChaptersInBook ?: 0,
                            totalPage = pi?.numberOfPagesInBook ?: 0
                        )
                    )
                }
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


    fun setScanListener(
        scanFinishedListener: (totalPage: Int) -> Unit,
        getNavListener: (List<ViewerIndexData<NavPoint>>) -> Unit
    ) {
        // set the pagingListener which is called when GlobalPagination is true.
        // this enables the calculation for the total number of pages in book, not in chapter.
        setPagingListener(object : PagingListener {
            override fun onPagingStarted(p0: Int) {
                Timber.v("epub log onPagingStarted: $p0")
            }

            /** called when the pagination of one chapter is finished  */
            override fun onPaged(p0: PagingInformation?) {
                p0?.let {
                    savedPagingInformation.add(it)
                }
            }

            /** called when all global pagination is over  */
            override fun onPagingFinished(bookCode: Int) {
                // do nothing
            }

            /** should return the number of pages  for given pagingInfromation.  */
            override fun getNumberOfPagesForPagingInformation(p0: PagingInformation?): Int {
                val value = savedPagingInformation.find {
                    it.chapterIndex == p0?.chapterIndex
                }?.numberOfPagesInChapter ?: 0
                return value
            }

            override fun onScanStarted(p0: Int) {
                // do nothing
            }

            override fun onScanned(p0: ItemRef?) {
                // do nothing
            }

            override fun onScanFinished(p0: Int) {
                scanFinishedListener.invoke(numberOfPagesInBook)
                getNavListener.invoke(getNavData())
            }

            override fun onTextExtracted(p0: Int, p1: Int, p2: String?) {
                // do nothing
            }

            override fun getText(p0: Int, p1: Int): String {
                // do nothing
                return ""
            }

            /** should retuan all pagingInfromations for given bookCode  */
            override fun getAnyPagingInformations(
                bookCode: Int,
                numberOfChapters: Int
            ): ArrayList<PagingInformation> {
                return savedPagingInformation
            }

            /** should return pagingInformation for given pagingInfromation.  */
            override fun getPagingInformation(p0: PagingInformation?): PagingInformation? {
                val value = savedPagingInformation.find {
                    it.bookCode == p0?.bookCode && it.chapterIndex == p0.chapterIndex
                }
                return value
            }

        })
    }

    companion object {
        private const val FONT_SIZE_NAME = "fontSize"
    }
}
