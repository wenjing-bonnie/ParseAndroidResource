package com.wj.parse.androidresource.parse

import com.wj.parse.androidresource.entity.package3.ResTablePackageThirdChunk
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolSecondChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.utils.Logger
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipFile

/**
 * the whole resource byte array
 */
class ParseResourceChain() {

    fun startParseResource(resourcePath: String) {
        val result = getResourceFromInputStream(FileInputStream(File(resourcePath)))

        result?.let { resourceByteArray ->

            /** read [ResourceTableHeaderFirstChunk]*/
            var parentOffset = 0
            Logger.debug("\n ...... begin to read first chunk: Table Header ...... parentOffset is $parentOffset")
            val tableHeaderChunk = ResourceTableHeaderFirstChunk(resourceByteArray).apply {
                Logger.debug(toString())
            }
            /** read [ResStringPoolSecondChunk] */
            parentOffset += tableHeaderChunk.chunkEndOffset
            Logger.debug("\n ...... begin to read second chunk: String Pool ...... parentOffset is $parentOffset")
            val stringPoolChunk =
                ResStringPoolSecondChunk(resourceByteArray, parentOffset).apply {
                    Logger.debug(toString())
                }
            /** read [ResTablePackageThirdChunk] */
            parentOffset += stringPoolChunk.chunkEndOffset
            Logger.debug("\n ...... begin to read third chunk: table package ...... parentOffset is $parentOffset")
            val tablePackageChunk = ResTablePackageThirdChunk(resourceByteArray,parentOffset,)


        } ?: run {
            Logger.error("The resource data is null, there is something wrong.")
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
}