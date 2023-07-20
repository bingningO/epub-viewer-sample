package com.bing.epublib.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EpubInfoDao {
    @Query("SELECT * FROM epub_info WHERE fileCode = :fileCode")
    suspend fun getEpubInfo(fileCode: String): EpubInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(epubInfo: EpubInfoEntity)

    @Update
    suspend fun update(epubInfo: EpubInfoEntity)

    @Query("DELETE FROM epub_info WHERE fileCode = :fileCode")
    suspend fun delete(fileCode: String)

    @Query("DELETE FROM epub_info")
    suspend fun deleteAll()
}