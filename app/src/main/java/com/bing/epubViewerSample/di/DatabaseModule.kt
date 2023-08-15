package com.bing.epubViewerSample.di

import android.content.Context
import com.bing.epubViewerSample.data.EpubInfoDao
import com.bing.epubViewerSample.data.EpubInfoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

//    @Provides
//    @Singleton
//    fun providesEpubInfoDatabase(context: Context): EpubInfoDatabase =
//        EpubInfoDatabase.buildDatabase(context)

    @Provides
    @Singleton
    fun providesEpubInfoDao(context: Context): EpubInfoDao {
        val db = EpubInfoDatabase.buildDatabase(context)
        return db.epubInfoDao()
    }

}