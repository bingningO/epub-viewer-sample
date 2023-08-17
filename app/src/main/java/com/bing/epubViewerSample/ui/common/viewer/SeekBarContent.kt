package com.bing.epubViewerSample.ui.common.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.bing.epubViewerSample.R
import com.bing.epubViewerSample.model.FontSize
import com.bing.epubViewerSample.ui.theme.Paddings
import com.bing.epubViewerSample.ui.theme.epubViewerSampleTheme

/**
 * @param onChangeSeekbarProgressFinish 0f ~ 1f
 */
@Composable
fun SeekBarContent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    onIndexClick: () -> Unit,
    onFontSizeSelected: (FontSize) -> Unit,
    currentIndex: Int,
    totalPage: Int,
    onChangeSeekbarProgressFinish: (globalIndex: Int) -> Unit,
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            ViewerControllerSeekbarWithPageIndexText(
                modifier = Modifier.fillMaxWidth(),
                currentIndex = currentIndex,
                totalPage = totalPage,
                onChangeSeekbarProgressFinish = onChangeSeekbarProgressFinish,
            )

            SettingButtonWithPopup(
                textResId = R.string.font_size,
                menuItems = arrayOf(FontSize.BIGGER, FontSize.SMALLER),
                onItemSelect = {
                    onFontSizeSelected(it)
                },
                selected = null
            )
        }
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
    onCloseClick: () -> Unit,
    onIndexClick: () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        IconButton(
            onClick = onCloseClick,
        ) {
            Icon(
                modifier = modifier
                    .align(Alignment.TopStart)
                    .size(dimensionResource(R.dimen.ui_common_app_bar_height))
                    .padding(4.dp),
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Close",
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = Paddings.default)
        ) {
            IconButton(
                onClick = onIndexClick,
            ) {
                Icon(
                    modifier = modifier
                        .size(dimensionResource(R.dimen.ui_common_app_bar_height))
                        .padding(4.dp),
                    painter = painterResource(R.drawable.ic_index),
                    contentDescription = "table of content",
                )
            }
        }
    }
}

@Preview
@Composable
private fun SeekBarContentPreview() {
    epubViewerSampleTheme {
        SeekBarContent(
            onClick = {},
            onCloseClick = {},
            onIndexClick = {},
            onFontSizeSelected = {},
            currentIndex = 25,
            totalPage = 100,
            onChangeSeekbarProgressFinish = {},
        )
    }
}
