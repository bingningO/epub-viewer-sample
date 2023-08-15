package com.bing.epubViewerSample.ui.skyEpub

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.bing.epubViewerSample.ui.common.viewer.ViewerIndexData
import com.skytree.epub.ClickListener
import com.skytree.epub.FixedControl
import com.skytree.epub.NavPoint
import com.skytree.epub.PageInformation
import com.skytree.epub.PageMovedListener
import com.skytree.epub.PageTransition
import com.skytree.epub.Setting
import com.skytree.epub.State

/**
 * custom viewer extending SkyEpub SDK#FixedControl
 * todo WIP
 */
class SkyEpubFixedControl(context: Context, bookCodeValue: Int) : FixedControl(context),
    SkyEpubControlInterface {

    init {
        setBackgroundColor(Color.Gray.toArgb())
        setPageTransition(PageTransition.Curl)
        bookCode = bookCodeValue

//        isGlobalPagination = true
    }

    override fun setLoadingListener(listener: (isLoading: Boolean) -> Unit) {
        setStateListener { state ->
            val isLoading = when (state) {
                State.LOADING, State.BUSY -> true
                else -> false
            }
            listener.invoke(isLoading)
        }
    }

    override fun setOnPageMovedListener(listener: (SkyEpubViewerContract.BookPagingInfo) -> Unit) {
        setPageMovedListener(object : PageMovedListener {

            override fun onPageMoved(pi: PageInformation?) {
                // be notice [totalPage] is changed during pagination
                listener.invoke(
                    SkyEpubViewerContract.BookPagingInfo(
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

    override fun setOnScreenClicked(onScreenClicked: () -> Unit) {
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

    override fun setScanListener(
        listener: (totalPage: Int) -> Unit,
        getNavListener: (List<ViewerIndexData<NavPoint>>) -> Unit
    ) {
//        listener.invoke(pageCount)
//        getNavListener.invoke(getNavData())
    }

    private fun getNavData(): List<ViewerIndexData<NavPoint>> {
        val navDatas = navPoints
        val navList = mutableListOf<ViewerIndexData<NavPoint>>()
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

    private fun showToast(msg: String?) {
        if (Setting.isDebug()) {
            val toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}
