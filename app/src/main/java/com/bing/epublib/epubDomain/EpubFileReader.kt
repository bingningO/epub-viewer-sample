package com.bing.epublib.epubDomain

import android.content.Context
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class EpubFileReader @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted private val fileName: String,
    private val epubMetaInfoXmlParser: EpubMetaInfoXmlParser,
    private val epubMetaDataXmlParser: EpubMetaDataXmlParser,
) {
    private val dstEpubPath = "${context.filesDir.path}/${BOOK_PATH}$fileName"
    private val unpackDstPath = "${context.filesDir.path}/$UNPACK_BOOK_PATH/$fileName"
    private val unpackMetaInfoPath =
        "${context.filesDir.path}/${UNPACK_BOOK_PATH}$fileName$UNPACK_META_INFO_PATH"

    fun onClose() {
        // todo delete cache data / created files
    }

    suspend fun isFixedLayout(): Boolean {
        val metaInfoPath = getMetaInfoInputStream()?.let {
            epubMetaInfoXmlParser.getMetaInfoPath(it)
        } ?: throw Exception("analysis meta-info xml file, not find metaInfoPath")
        val layoutValue = getMetaDataInputStream(metaInfoPath)?.let {
            epubMetaDataXmlParser.getLayoutValue(it)
        }
        return EPUB_FIX_LAYOUT_VALUE == layoutValue
    }

    private fun getMetaInfoInputStream(): InputStream? {
        val metaInfoFile = File(unpackMetaInfoPath)
        if (metaInfoFile.exists().not()) {
            unpackZip("${context.filesDir.path}/${BOOK_PATH}", fileName)
        }
        return metaInfoFile.let {
            if (it.canRead()) {
                FileInputStream(it)
            } else {
                null
            }
        }
    }

    private fun getMetaDataInputStream(metaDataFileName: String): InputStream? {
        return File("$unpackDstPath/$metaDataFileName").let {
            if (it.canRead()) {
                FileInputStream(it)
            } else {
                null
            }
        }
    }

    @Throws(IOException::class)
    private fun unpackZip(path: String, name: String) {
        val `is`: InputStream = FileInputStream(path + name)
        val zis = ZipInputStream(BufferedInputStream(`is`))
        var ze: ZipEntry
        while (zis.nextEntry.also { ze = it } != null) {
            val baos = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var count: Int
            val fileName = ze.name
            val fout = FileOutputStream(dstEpubPath + fileName)

            // reading and writing
            while (zis.read(buffer).also { count = it } != -1) {
                baos.write(buffer, 0, count)
                val bytes = baos.toByteArray()
                fout.write(bytes)
                baos.reset()
            }
            fout.close()
            zis.closeEntry()
        }
        zis.close()
    }

    companion object {
        private const val EPUB_FIX_LAYOUT_VALUE = "pre-paginated"
        private const val BOOK_PATH = "books/"
        private const val UNPACK_BOOK_PATH = "unpack_books/"
        private const val UNPACK_META_INFO_PATH = "/META-INF/container.xml"
    }

    @AssistedFactory
    interface Factory {
        fun create(
            fileName: String
        ): EpubFileReader
    }
}