package com.bing.epubViewerSample.ui.common.viewer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bing.epublib.model.FontSize
import com.bing.epublib.model.PopupMenuItem

@Composable
fun <T> PopupMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    items: Array<T>,
    selected: T?,
    onSelect: (T) -> Unit,
    onDismissRequest: () -> Unit,
) where T : PopupMenuItem {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                onClick = { onSelect.invoke(item) },
                text = {
                    Text(
                        text = stringResource(id = item.textResId),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = if (selected == item) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            )
        }
    }
}

@Composable
@Preview
private fun PreviewPopupMenu() {
    PopupMenu(
        expanded = true,
        items = arrayOf(
            FontSize.BIGGER,
            FontSize.SMALLER,
        ),
        selected = null,
        onSelect = {},
        onDismissRequest = {},
    )
}
