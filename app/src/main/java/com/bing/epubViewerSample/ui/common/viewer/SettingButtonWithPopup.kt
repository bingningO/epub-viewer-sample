package com.bing.epubViewerSample.ui.common.viewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bing.epubViewerSample.model.FontSize
import com.bing.epubViewerSample.model.PopupMenuItem

@Composable
fun <T> SettingButtonWithPopup(
    modifier: Modifier = Modifier,
    textResId: Int,
    menuItems: Array<T>,
    onItemSelect: (T) -> Unit,
    selected: T?,
) where T : PopupMenuItem {
    var isFontPopupDisplay by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomStart),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            onClick = { isFontPopupDisplay = true },
            content = {
                Text(text = stringResource(id = textResId))
            }
        )

        PopupMenu(
            modifier = Modifier.align(Alignment.BottomStart),
            expanded = isFontPopupDisplay,
            items = menuItems,
            selected = selected,
            onSelect = onItemSelect,
            onDismissRequest = { isFontPopupDisplay = false },
        )
    }
}

@Composable
@Preview
private fun PreviewSettingButtonWithPopup() {
    Box(modifier = Modifier.fillMaxSize()) {
        SettingButtonWithPopup(
            modifier = Modifier.align(Alignment.BottomStart),
            textResId = android.R.string.ok,
            menuItems = arrayOf(
                FontSize.BIGGER,
                FontSize.SMALLER,
            ),
            selected = null,
            onItemSelect = {},
        )
    }
}

