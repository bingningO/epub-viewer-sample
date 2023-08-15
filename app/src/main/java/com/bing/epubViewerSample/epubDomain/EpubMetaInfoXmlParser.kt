package com.bing.epubViewerSample.epubDomain

import android.util.Xml
import com.bing.epublib.ui.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpubMetaInfoXmlParser @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : EpubXmlParser() {

    @Throws(XmlPullParserException::class, IOException::class)
    suspend fun getMetaInfoPath(inputStream: InputStream): String =
        withContext(dispatcher) {
            inputStream.use { inputS ->
                val parser: XmlPullParser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(inputS, null)
                parser.nextTag()
                return@withContext readContainer(parser)
            }
        }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readContainer(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, TAG_CONTAINER)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == TAG_ROOTFILES) {
                val value = readRootFiles(parser)
                if (value.isNotEmpty()) return value
            } else {
                skip(parser)
            }
        }
        return ""
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readRootFiles(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, TAG_ROOTFILES)

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                TAG_ROOTFILE -> {
                    val value = readFullPathProperty(parser)
                    if (value.isNotEmpty()) return value
                }

                else -> skip(parser)
            }
        }
        return ""
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readFullPathProperty(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, TAG_ROOTFILE)
        return parser.getAttributeValue(null, FULL_PATH_ATTRIBUTE)
    }

    companion object {
        private const val TAG_CONTAINER = "container"
        private const val TAG_ROOTFILES = "rootfiles"
        private const val TAG_ROOTFILE = "rootfile"
        private const val FULL_PATH_ATTRIBUTE = "full-path"
    }
}
