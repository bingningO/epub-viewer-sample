package com.bing.epublib.ui.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bing.epublib.R
import com.bing.epublib.ui.theme.EpubLibTheme
import com.bing.epublib.ui.theme.Paddings

@Composable
fun SlidePanel(
    modifier: Modifier = Modifier,
    title: String,
    onCloseClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .windowInsetsPadding(
                        WindowInsets.statusBars.only(
                            WindowInsetsSides.Top
                        )
                    )
                    .height(dimensionResource(R.dimen.ui_common_app_bar_height)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onCloseClick,
                ) {
                    Icon(
                        modifier = modifier
                            .size(dimensionResource(R.dimen.ui_common_app_bar_height))
                            .padding(12.dp),
                        painter = painterResource(R.drawable.ui_common_ic_mirai_ui_close),
                        contentDescription = "Close",
                    )
                }

                Text(
                    modifier = Modifier.padding(horizontal = Paddings.default),
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

            }

            content.invoke()
        }
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
fun PreviewSlidePanel() {
    EpubLibTheme {
        SlidePanel(
            title = "Title",
            onCloseClick = {},
            content = {
                Box(modifier = Modifier.fillMaxSize())
            },
        )
    }
}
