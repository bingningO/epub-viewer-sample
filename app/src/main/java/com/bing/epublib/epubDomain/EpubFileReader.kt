package com.bing.epublib.epubDomain

import android.content.Context
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
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
    private val dstEpubPath = "${context.filesDir.path}/${BOOK_PATH}"
    private val unpackDstPath = "${context.filesDir.path}/$UNPACK_BOOK_PATH"
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
            unzip(
                zipFile = File(dstEpubPath + fileName),
                targetDirectory = File("$unpackDstPath/$fileName")
            )
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
        return File("$unpackDstPath/$fileName/$metaDataFileName").let {
            if (it.canRead()) {
                FileInputStream(it)
            } else {
                null
            }
        }
    }

    @Throws(IOException::class)
    fun unzip(zipFile: File?, targetDirectory: File?) {
        val zis = ZipInputStream(
            BufferedInputStream(FileInputStream(zipFile))
        )
        zis.use { zis ->
            var ze: ZipEntry
            var count: Int
            val buffer = ByteArray(8192)
            while (null != zis.nextEntry.also { ze = it ?: return@use }) {
                val file = File(targetDirectory, ze.name)
                val dir = if (ze.isDirectory) file else file.parentFile
                if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException(
                    "Failed to ensure directory: " +
                            dir.absolutePath
                )
                if (ze.isDirectory) continue
                val fout = FileOutputStream(file)
                try {
                    while (zis.read(buffer).also { count = it } != -1) fout.write(buffer, 0, count)
                } finally {
                    fout.close()
                }
                /* if time should be restored as well
                long time = ze.getTime();
                if (time > 0)
                    file.setLastModified(time);
                */
            }
        }
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