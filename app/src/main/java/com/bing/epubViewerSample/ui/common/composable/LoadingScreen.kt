package com.bing.epubViewerSample.ui.common.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bing.epubViewerSample.ui.theme.epubViewerSampleTheme

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(82.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
private fun PreviewLoadingScreen() {
    epubViewerSampleTheme {
        LoadingScreen()
    }
}