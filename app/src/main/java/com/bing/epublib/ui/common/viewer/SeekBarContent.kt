package com.bing.epublib.ui.common.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bing.epublib.R
import com.bing.epublib.ui.theme.EpubLibTheme
import com.bing.epublib.ui.theme.Paddings

/**
 * @param onChangeSeekbarProgressFinish 0f ~ 1f
 */
@Composable
fun SeekBarContent(
    modifier: Modifier = Modifier,
    currentIndex: Int,
    totalPage: Int,
    onChangeSeekbarProgressFinish: (globalIndex: Int) -> Unit,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    onIndexClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
    ) {
        ViewerControllerBackground()

        ViewerControllerTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            onCloseClick = onCloseClick,
            onIndexClick = onIndexClick,
        )

        ViewerControllerSeekbarWithPageIndexText(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            currentIndex = currentIndex,
            totalPage = totalPage,
            onChangeSeekbarProgressFinish = onChangeSeekbarProgressFinish
        )
    }
}

@Composable
private fun ViewerControllerBackground(
    modifier: Modifier = Modifier,
) {
    val topGradient =
        Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to MaterialTheme.colorScheme.background.copy(alpha = 0.2F),
                1.0f to Color.Transparent,
            ),
            startY = 0.0f,
            endY = Float.POSITIVE_INFINITY,
        )

    val bottomGradient =
        Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to Color.Transparent,
                1.0f to MaterialTheme.colorScheme.background.copy(alpha = 0.5F),
            ),
            startY = 0.0f,
            endY = Float.POSITIVE_INFINITY,
        )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.2f)),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(88.dp)
                .background(topGradient),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(320.dp)
                .background(bottomGradient),
        )
    }
}

@Composable
private fun ViewerControllerTopBar(
    modifier: Modifier = Modifier,
//    showSearchButton: Boolean,
    onCloseClick: () -> Unit,
    onIndexClick: () -> Unit,
//    onSearchClick: () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        IconButton(
            onClick = onCloseClick,
        ) {
            Icon(
                modifier = modifier
                    .align(Alignment.TopStart)
                    .size(dimensionResource(R.dimen.ui_common_app_bar_height))
                    .padding(12.dp),
                painter = painterResource(R.drawable.ui_common_ic_mirai_ui_close),
                contentDescription = "Close",
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = Paddings.default)
        ) {
//            if (showSearchButton) {
//                IconButton(
//                    onClick = onSearchClick,
//                ) {
//                    Icon(
//                        modifier = modifier
//                            .size(dimensionResource(jp.unext.mediaplayer.common.core.R.dimen.ui_common_app_bar_height))
//                            .padding(16.dp),
//                        painter = painterResource(jp.unext.mediaplayer.common.core.R.drawable.ui_common_ic_mirai_ui_search),
//                        contentDescription = stringResource(jp.unext.mediaplayer.common.core.R.string.ui_common_search),
//                    )
//                }
//            }

            IconButton(
                onClick = onIndexClick,
            ) {
                Icon(
                    modifier = modifier
                        .size(dimensionResource(R.dimen.ui_common_app_bar_height))
                        .padding(8.dp),
                    painter = painterResource(R.drawable.book_viewer_ic_book_index),
                    contentDescription = "table of content",
                )
            }
        }
    }
}

@Preview
@Composable
private fun SeekBarContentPreview() {
    EpubLibTheme {
        SeekBarContent(
            currentIndex = 12,
            totalPage = 60,
            onChangeSeekbarProgressFinish = {},
            onClick = { /*TODO*/ },
            onCloseClick = { /*TODO*/ }) {
        }
    }
}
