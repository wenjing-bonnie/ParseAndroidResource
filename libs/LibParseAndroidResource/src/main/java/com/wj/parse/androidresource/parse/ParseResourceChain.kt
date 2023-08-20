package com.wj.parse.androidresource.parse

import com.wj.parse.androidresource.entity.package3.ResTablePackageHeaderThirdChunk
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolSecondChunk
import com.wj.parse.androidresource.entity.stringpool4.ResTypeStringPoolFourChunk
import com.wj.parse.androidresource.entity.stringpool5.ResKeyStringsPoolFiveChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.entity.typespec6.ResTypeSpecAndTypeInfoSixChunk
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
            tableHeaderChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

            /** read [ResStringPoolSecondChunk] */
            parentOffset += tableHeaderChunk.chunkEndOffset
            Logger.debug("\n ...... begin to read second chunk: String Pool ...... parentOffset is $parentOffset")
            val stringPoolChunk =
                ResStringPoolSecondChunk(resourceByteArray, parentOffset)
            stringPoolChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

            /** read [ResTablePackageHeaderThirdChunk] */
            parentOffset += stringPoolChunk.chunkEndOffset
            Logger.debug("\n ...... begin to read third chunk: table package ...... parentOffset is $parentOffset")
            val tablePackageChunk =
                ResTablePackageHeaderThirdChunk(resourceByteArray, parentOffset)
            tablePackageChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

            /** read [ResTypeStringPoolFourChunk] */
            val typeStringOffset = parentOffset + tablePackageChunk.typeStrings
            Logger.debug("\n ...... begin to read four chunk: type string pool ...... parentOffset is $typeStringOffset")
            val typeStringPoolChunk =
                ResTypeStringPoolFourChunk(resourceByteArray, typeStringOffset)
            typeStringPoolChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

            /** read [ResKeyStringsPoolFiveChunk] */
            val keyStringsOffset = parentOffset + tablePackageChunk.keyStrings
            Logger.debug("\n ...... begin to read five chunk: key strings pool ...... parentOffset is $keyStringsOffset")
            val keyStringsPoolChunk =
                ResKeyStringsPoolFiveChunk(resourceByteArray, keyStringsOffset)
            keyStringsPoolChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

            /** read [ResTypeSpecAndTypeInfoSixChunk] */
            parentOffset = keyStringsOffset + keyStringsPoolChunk.chunkEndOffset
            Logger.debug("\n ...... begin to read six chunk: type spec and type info  ...... parentOffset is $parentOffset")
            val typeChunk = ResTypeSpecAndTypeInfoSixChunk(resourceByteArray, parentOffset)
            typeChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

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