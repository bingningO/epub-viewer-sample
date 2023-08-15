package com.bing.epubViewerSample.repository

import com.bing.epubViewerSample.data.EpubInfoDao
import com.bing.epubViewerSample.data.EpubInfoEntity
import com.bing.epubViewerSample.di.ApplicationScope
import com.bing.epubViewerSample.model.EpubInfo
import com.bing.epubViewerSample.model.EpubSetting
import com.bing.epubViewerSample.ui.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class EpubInfoRepositoryImpl @Inject constructor(
    private val dao: EpubInfoDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val appScope: CoroutineScope,
) : EpubInfoRepository {
    override suspend fun getEpubInfo(fileCode: Int): EpubInfo? =
        with(appScope.coroutineContext) {
            return@with dao.getEpubInfo(fileCode)?.toModule()
        }

    override suspend fun insertEpubInfo(epubInfo: EpubInfo) =
        withContext(appScope.coroutineContext) {
            dao.insert(epubInfo.toEntity())
        }

    override suspend fun deleteEpubInfo(fileCode: Int) =
        withContext(appScope.coroutineContext) {
            dao.delete(fileCode)
        }

    override suspend fun deleteAll() = withContext(appScope.coroutineContext) {
        dao.deleteAll()
    }
}

private fun EpubInfoEntity.toModule(): EpubInfo = EpubInfo(
    fileCode = this.fileCode,
    startPositionInBook = this.startPositionInBook,
    setting = EpubSetting(
        fontSize = this.fontSize,
        colorFilter = this.colorFilter,
        pageEffect = this.pageEffect,
    )
)

private fun EpubInfo.toEntity(): EpubInfoEntity = EpubInfoEntity(
    fileCode = this.fileCode,
    startPositionInBook = this.startPositionInBook,
    fontSize = this.setting?.fontSize ?: 0,
    colorFilter = this.setting?.colorFilter ?: 0,
    pageEffect = this.setting?.pageEffect ?: 0,
)
