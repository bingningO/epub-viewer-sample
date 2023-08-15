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

/**
 * parse and get layout value from xml meta file of EPUB
 * it should name as [item/standard.opf] and looks like below
 * <package …>
 *   <metadata …>
 *   …
 *     <meta
 *       property="rendition:layout">
 *       pre-paginated
 *     </meta>
 *     …
 *   </metadata>
 *   …
 * </package>
 *
 * and [property="rendition:layout"] can be only defined once in meta file.
 *
 * refer for meta file of Epub: https://www.w3.org/TR/epub-33/#sec-general-rendering-intro
 * refer for how to parse xml: https://developer.android.com/training/basics/network-ops/xml
 */
@Singleton
class EpubMetaDataXmlParser @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : EpubXmlParser() {

    @Throws(XmlPullParserException::class, IOException::class)
    suspend fun getLayoutValue(inputStream: InputStream): String =
        withContext(dispatcher) {
            inputStream.use { inputS ->
                val parser: XmlPullParser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(inputS, null)
                parser.nextTag()
                return@withContext readPackage(parser)
            }
        }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readPackage(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, TAG_PACKAGE)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == TAG_METADATA) {
                val value = readMetadata(parser)
                if (value.isNotEmpty()) return value
            } else {
                skip(parser)
            }
        }
        return ""
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readMetadata(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, TAG_METADATA)

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                TAG_META -> {
                    val value = readLayoutProperty(parser)
                    if (value.isNotEmpty()) return value
                }

                else -> skip(parser)
            }
        }
        return ""
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readLayoutProperty(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, TAG_META)
        val relType = parser.getAttributeValue(null, ATTRIBUTE_PROPERTY)
        if (relType == PROPERTY_LAYOUT && parser.next() == XmlPullParser.TEXT) {
            val layoutProperty = parser.text
            if (layoutProperty.isNotEmpty()) return layoutProperty
            parser.nextTag()
        } else {
            skip(parser)
        }
        parser.require(XmlPullParser.END_TAG, null, TAG_META)
        return ""
    }

    companion object {
        private const val TAG_PACKAGE = "package"
        private const val TAG_METADATA = "metadata"
        private const val TAG_META = "meta"
        private const val ATTRIBUTE_PROPERTY = "property"
        private const val PROPERTY_LAYOUT = "rendition:layout"
    }
}
