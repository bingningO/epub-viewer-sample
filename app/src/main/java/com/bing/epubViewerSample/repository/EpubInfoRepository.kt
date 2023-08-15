package com.bing.epubViewerSample.repository

import com.bing.epubViewerSample.model.EpubInfo

interface EpubInfoRepository {
    suspend fun getEpubInfo(fileCode: Int): EpubInfo?
    suspend fun insertEpubInfo(epubInfo: EpubInfo)
    suspend fun deleteEpubInfo(fileCode: Int)
    suspend fun deleteAll()
}
