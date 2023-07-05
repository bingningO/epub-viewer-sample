package com.bing.epublib.ui.common.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bing.epublib.ui.theme.EpubLibTheme

@Composable
fun ErrorScreen(error: Throwable) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Got error when loading\n${error.message ?: "Unknown error"}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Preview
@Composable
private fun PreviewErrorScreen() {
    EpubLibTheme {
        ErrorScreen(error = Throwable("Test error"))
    }
}
