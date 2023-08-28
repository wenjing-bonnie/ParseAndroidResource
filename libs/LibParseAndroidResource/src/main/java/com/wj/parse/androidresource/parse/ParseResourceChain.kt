package com.wj.parse.androidresource.parse

import com.wj.parse.androidresource.entity.package3.ResTablePackageThirdChunk
import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolSecondChunk
import com.wj.parse.androidresource.entity.stringpool4.ResTypeStringPoolFourChunk
import com.wj.parse.androidresource.entity.stringpool5.ResKeyStringsPoolFiveChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeSixChunk
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
            Logger.debug("\n${Logger.TITLE_TAG_START} Begin to read first chunk \"Table Header\", parentOffset is $parentOffset ${Logger.TITLE_TAG_END}")
            val tableHeaderChunk = ResourceTableHeaderFirstChunk(resourceByteArray)
            tableHeaderChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

            /** read [ResGlobalStringPoolSecondChunk] */
            parentOffset += tableHeaderChunk.chunkEndOffset
            Logger.debug("\n${Logger.TITLE_TAG_START} Begin to read second chunk: GlobalString Pool, parentOffset is $parentOffset ${Logger.TITLE_TAG_END},")
            val stringPoolChunk =
                ResGlobalStringPoolSecondChunk(resourceByteArray, parentOffset)
            stringPoolChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

            /** read [ResTablePackageThirdChunk] */
            parentOffset += stringPoolChunk.chunkEndOffset
            Logger.debug("\n${Logger.TITLE_TAG_START} begin to read third chunk: table package, parentOffset is $parentOffset ${Logger.TITLE_TAG_END}")
            val tablePackageChunk =
                ResTablePackageThirdChunk(resourceByteArray, parentOffset)
            tablePackageChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

            /** read [ResTypeStringPoolFourChunk] */
            val typeStringOffset = parentOffset + tablePackageChunk.typeStrings
            Logger.debug("\n${Logger.TITLE_TAG_START} begin to read four chunk: Type String Pool, parentOffset is $typeStringOffset  ${Logger.TITLE_TAG_END}")
            val typeStringPoolChunk =
                ResTypeStringPoolFourChunk(resourceByteArray, typeStringOffset)
            typeStringPoolChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

            /** read [ResKeyStringsPoolFiveChunk] */
            val keyStringsOffset = parentOffset + tablePackageChunk.keyStrings
            Logger.debug("\n${Logger.TITLE_TAG_START} begin to read five chunk: Key String Pool, parentOffset is $keyStringsOffset ${Logger.TITLE_TAG_END}")
            val keyStringsPoolChunk =
                ResKeyStringsPoolFiveChunk(resourceByteArray, keyStringsOffset)
            keyStringsPoolChunk.startParseChunk().also {
                Logger.debug(it.toString())
            }

            /** read [ResTableTypeSpecAndTypeSixChunk] */
            parentOffset = keyStringsOffset + keyStringsPoolChunk.chunkEndOffset
            Logger.debug("\n${Logger.TITLE_TAG_START} begin to read six chunk: table type spec and table type, parentOffset is $parentOffset ${Logger.TITLE_TAG_END}")
            val typeChunk = ResTableTypeSpecAndTypeSixChunk(
                resourceByteArray,
                parentOffset,
                globalStringList = stringPoolChunk.resStringPoolRefOffset.globalStringList,
                typeStringList = typeStringPoolChunk.resStringPoolRefOffset.globalStringList,
                keyStringList = keyStringsPoolChunk.resStringPoolRefOffset.globalStringList,
                packageId = tablePackageChunk.id
            )
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