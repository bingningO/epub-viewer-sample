package com.bing.epublib.ui.common

import java.util.UUID

open class UIEvent {
    val id: Long = UUID.randomUUID().mostSignificantBits
}