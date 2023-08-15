package com.bing.epubViewerSample.di

import com.bing.epublib.repository.EpubInfoRepository
import com.bing.epublib.repository.EpubInfoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    internal abstract fun bindsEpubInfoRepository(impl: EpubInfoRepositoryImpl): EpubInfoRepository
}
