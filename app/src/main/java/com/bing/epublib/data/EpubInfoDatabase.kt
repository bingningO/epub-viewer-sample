package com.bing.epublib.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [EpubInfoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EpubInfoDatabase : RoomDatabase() {
    abstract fun epubInfoDao(): EpubInfoDao

    companion object {
        private const val DATABASE_NAME = "epub_info.db"

        fun buildDatabase(context: Context): EpubInfoDatabase {
            return Room.databaseBuilder(context, EpubInfoDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }
}