package com.wj.parse.androidresource

import com.wj.parse.androidresource.parse.ParseResourceChain
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipFile

/**
 * https://developer.android.com/guide/topics/resources/string-resource
 * https://docs.fileformat.com/programming/arsc/
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h
 * https://www.jianshu.com/p/b04e5b4806a7?utm_campaign=maleskine...&utm_content=note&utm_medium=seo_notes&utm_source=recommendation
 */
fun main(args: Array<String>) {
    // val result = getResourceFromApkFile("/Users/wendli/Downloads/230719460.apk")

    val resourcePath =
        "/Users/liuwenjing/Documents/code/ParseAndroidResource/libs/LibParseAndroidResource/src/main/resources/resources.arsc"
    val result = getResourceFromInputStream(FileInputStream(File(resourcePath)))

    result?.let { array ->
        val manager = ParseResourceChain(array)

//        /** read [ResourceTableHeaderFirstChunk]*/
//        var offset = 0
//        Logger.debug("begin to read first chunk: Table Header ......")
//        val tableHeader = manager.parseResourceTableHeaderFirstChunk(array)
//        offset += tableHeader.chunkEndOffset
//
//        /** read [ResStringPoolHeaderSecondChunk] */
//        Logger.debug("begin to read second chunk: String Pool Header......")
//        val stringPoolHeaderArray = Utils.copyByte(array, offset)
//        var stringPoolHeader: ResStringPoolHeaderSecondChunk? = null
//        stringPoolHeaderArray?.let {
//            stringPoolHeader = manager.parseResStringPoolHeaderSecondChunk(it)
//        } ?: run {
//            Logger.debug("Can't read the ResStringPoolHeader data.")
//        }
//
//        /** read [ResStringPoolRefOffsetSecondChunk]*/
//        Logger.debug("begin to read second chunk: String Pool ref ......")
//        var stringPoolRes: ResStringPoolRefOffsetSecondChunk? = null
//        stringPoolHeader?.let { header ->
//            offset += header.chunkEndOffset
//            val stringPoolRefArray = Utils.copyByte(array, offset)
//            stringPoolRefArray?.let {
//                stringPoolRes = manager.parseResStringPoolRefSecondChunk(it)
//            } ?: run {
//                Logger.debug("Can't read the ResStringPoolRefSecondChunk data.")
//            }
//        } ?: run {
//            Logger.debug("ResStringPoolHeader data is null.")
//        }
//
//        /** */
//        stringPoolRes?.let {
//
//        } ?: run {
//            Logger.debug("ResStringPoolHeader data is null.")
//        }
//    } ?: run {
//        Logger.error("The resource data is null, there is something wrong.")
    }
}

fun getResourceFromInputStream(resourceInputStream: InputStream): ByteArray? {
    var srcByteArray: ByteArray? = null
    var inputStream: InputStream? = null
    var byteOutputStream: ByteArrayOutputStream? = null

    try {
        inputStream = resourceInputStream
        byteOutputStream = ByteArrayOutputStream()
        var buffer = ByteArray(1024)
        var length = 0
        while (inputStream.read(buffer).also { length = it } != -1) {
            byteOutputStream.write(buffer, 0, length)
        }
        srcByteArray = byteOutputStream.toByteArray()
    } catch (e: Exception) {

    } finally {
        inputStream?.close()
        byteOutputStream?.close()
    }
    return srcByteArray
}

fun getResourceFromApkFile(apkPath: String): ByteArray? {
    var srcByteArray: ByteArray? = null
    var apkFile: ZipFile? = null
    try {
        apkFile = ZipFile(apkPath)
        val entry = apkFile.getEntry("resources.arsc")
        srcByteArray = getResourceFromInputStream(apkFile.getInputStream(entry))
        apkFile.close()
    } catch (e: Exception) {

    } finally {
        apkFile?.close()
    }
    return srcByteArray
}