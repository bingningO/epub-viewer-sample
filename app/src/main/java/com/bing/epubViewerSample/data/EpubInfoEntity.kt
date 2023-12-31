package com.bing.epubViewerSample.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "epub_info")
data class EpubInfoEntity(
    @PrimaryKey val fileCode: Int,
    @ColumnInfo(name = "start_position_in_book") val startPositionInBook: Double,
    @ColumnInfo(name = "font_size") val fontSize: Int,
    @ColumnInfo(name = "color_filter") val colorFilter: Int,
    @ColumnInfo(name = "page_effect") val pageEffect: Int,
)
