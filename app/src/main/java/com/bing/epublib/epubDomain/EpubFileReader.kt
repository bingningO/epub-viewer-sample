package com.bing.epublib.epubDomain

import android.content.Context
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.ZipInputStream

class EpubFileReader @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted private val fileName: String,
    private val epubMetaXmlParser: EpubMetaXmlParser,
) {
    private val dstPath = "${context.filesDir.path}/${BOOK_PATH}"

    private val entryOffsetMap: MutableMap<String, Long> by lazy {
        val map: MutableMap<String, Long> = mutableMapOf()
        try {
            getInputStream()?.apply {
                val b = ByteArray(HEADER_SIZE)
                read(b, 0, HEADER_SIZE)
                val recordNum = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt(12)
                for (i in 0 until recordNum) {
                    read(b, 0, HEADER_SIZE)
                    val recordSize = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt(0)
                    val fileOffset = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getLong(4)
                    val stringLength = recordSize - HEADER_SIZE
                    val bs = ByteArray(stringLength)
                    read(bs, 0, stringLength)
                    val entryName = String(bs)
                    map[entryName] = fileOffset
                }
                close()
            }
        } catch (th: Throwable) {
            Timber.e(th)
        }
        map
    }

    suspend fun isFixedLayout(): Boolean {
        val layoutValue = getEpubMetaInputStream()?.let {
            epubMetaXmlParser.getLayoutValue(it)
        }
        return EPUB_FIX_LAYOUT_VALUE == layoutValue
    }

    private fun getEpubMetaInputStream(): InputStream? {
        var b: ByteArray = byteArrayOf()
        entryOffsetMap.keys.forEach { entryName ->
            if (entryName == EPUB_META_FILE_NAME) {
                val offset: Long = entryOffsetMap[entryName] ?: return@forEach
                getInputStream()?.let { inputStream ->
                    inputStream.skip(offset)
                    ZipInputStream(inputStream).run {
                        val zipEntry = nextEntry
                        b = ByteArray(zipEntry.size.toInt())
                        buffered().read(b)
                        close()
                        inputStream.close()
                        return@forEach
                    }
                }
            }
        }
        return if (b.isEmpty()) {
            null
        } else {
            ByteArrayInputStream(b)
        }
    }

    private fun getInputStream(): FileInputStream? {
        return File("$dstPath$fileName").let {
            if (it.canRead()) {
                FileInputStream(it)
            } else {
                null
            }
        }
    }

    companion object {
        private const val HEADER_SIZE = 16
        private const val EPUB_META_FILE_NAME = "item/standard.opf"
        private const val EPUB_FIX_LAYOUT_VALUE = "pre-paginated"
        private const val BOOK_PATH = "books/"
    }

    @AssistedFactory
    interface Factory {
        fun create(
            fileName: String
        ): EpubFileReader
    }
}