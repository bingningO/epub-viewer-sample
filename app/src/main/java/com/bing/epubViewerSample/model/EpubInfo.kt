package com.bing.epubViewerSample.model

data class EpubInfo(
    val fileCode: Int,
    val startPositionInBook: Double,
    val setting: EpubSetting? = null,
)

data class EpubSetting(
    val fontSize: Int = 0,
    val colorFilter: Int = 0,
    val pageEffect: Int = 0,
)