package com.bing.epublib.epubDomain

import android.content.Context
import com.bing.epublib.ui.common.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * copy book file from assets to app files dir
 */
@Singleton
class EpubFileHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val dstPath = "${context.filesDir.path}/$BOOK_PATH"

    @Throws(IOException::class)
    suspend fun prepareBook(fileName: String) {
        val dstFileDir = File(dstPath)
        if (dstFileDir.mkdir()) {
            copyFileIfSpaceEnough(fileName)
        } else {
            if (checkIfNeedCopy(fileName)) {
                copyFileIfSpaceEnough(fileName)
            }
        }
    }

    private suspend fun checkIfNeedCopy(fileName: String) = withContext(ioDispatcher) {
        val file = File(dstPath, fileName)
        if (file.exists().not()) return@withContext true else false
        // todo also check hash value
    }

    @Throws(IOException::class)
    private suspend fun copyFileIfSpaceEnough(fileName: String) {
        // todo check if dst path has enough space
        withContext(ioDispatcher) {
            context.assets.open("$BOOK_PATH$fileName").use { input ->
                FileOutputStream("$dstPath$fileName").use { output ->
                    val buff = ByteArray(INPUT_BUFF_SIZE)
                    var read = input.read(buff)
                    while (read > 0) {
                        output.write(buff, 0, read)
                        read = input.read(buff)
                    }
                }
            }
        }
    }

    fun getBookPath(fileName: String) = "$dstPath$fileName"

    companion object {
        private const val BOOK_PATH = "books/"
        private const val INPUT_BUFF_SIZE = 16 * 1024
    }
}