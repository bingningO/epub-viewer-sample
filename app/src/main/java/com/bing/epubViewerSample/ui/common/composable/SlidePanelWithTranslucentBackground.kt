package com.bing.epubViewerSample.ui.common.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bing.epublib.ui.theme.EpubLibTheme

@Composable
fun SlidePanelWithTranslucentBackground(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    title: String,
    content: @Composable () -> Unit,
    onHide: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onHide)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
            )
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.CenterEnd),
            visible = isVisible,
            enter = slideInHorizontally { it / 2 } + fadeIn(),
            exit = slideOutHorizontally { it / 2 } + fadeOut(),
        ) {
            SlidePanel(
                modifier = modifier,
                title = title,
                onCloseClick = onHide,
                content = content,
            )
        }
    }
}

@Preview
@Composable
fun PreviewSlidePanelWithTranslucentBackground() {
    EpubLibTheme {
        SlidePanelWithTranslucentBackground(
            isVisible = true,
            title = "Title",
            onHide = {},
            content = {
                Box(modifier = Modifier.fillMaxSize())
            },
        )
    }
}
