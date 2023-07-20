package com.bing.epublib.repository

import com.bing.epublib.model.EpubInfo

interface EpubInfoRepository {
    suspend fun getEpubInfo(fileCode: String): EpubInfo?
    suspend fun insertEpubInfo(epubInfo: EpubInfo)
    suspend fun deleteEpubInfo(fileCode: String)
    suspend fun deleteAll()
}
