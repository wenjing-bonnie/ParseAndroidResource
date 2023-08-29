package com.wj.parse.androidresource.parse

import com.wj.parse.androidresource.entity.package3.ResTablePackageThirdChunk
import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolChunk
import com.wj.parse.androidresource.entity.stringpool4.ResTypeStringPoolChunk
import com.wj.parse.androidresource.entity.stringpool5.ResKeyStringsPoolChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderChunk
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
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

            /** read [ResourceTableHeaderChunk]*/
            var parentOffset = 0
            val tableHeaderChunk = ResourceTableHeaderChunk(resourceByteArray)
            logBeginningTitle(1, "ResTable_header", parentOffset, tableHeaderChunk)

            /** read [ResGlobalStringPoolChunk] */
            parentOffset += tableHeaderChunk.chunkEndOffset
            val stringPoolChunk =
                ResGlobalStringPoolChunk(resourceByteArray, parentOffset)
            stringPoolChunk.startParseChunk().also { globalPool ->
                logBeginningTitle(2, "Global String Pool", parentOffset, globalPool)
            }

            /** read [ResTablePackageThirdChunk] */
            parentOffset += stringPoolChunk.chunkEndOffset
            val tablePackageChunk =
                ResTablePackageThirdChunk(resourceByteArray, parentOffset)
            logBeginningTitle(3, "ResTable_package", parentOffset, tablePackageChunk)


            /** read [ResTypeStringPoolChunk] */
            val typeStringOffset = parentOffset + tablePackageChunk.typeStrings
            val typeStringPoolChunk =
                ResTypeStringPoolChunk(resourceByteArray, typeStringOffset)
            typeStringPoolChunk.startParseChunk().also { typePool ->
                logBeginningTitle(4, "Type String Pool,", parentOffset, typePool)
            }

            /** read [ResKeyStringsPoolChunk] */
            val keyStringsOffset = parentOffset + tablePackageChunk.keyStrings
            val keyStringsPoolChunk =
                ResKeyStringsPoolChunk(resourceByteArray, keyStringsOffset)
            keyStringsPoolChunk.startParseChunk().also { keyPool ->
                logBeginningTitle(5, "Key String Pool,", parentOffset, keyPool)
            }

            /** read [ResTableTypeSpecAndTypeChunk] */
            parentOffset = keyStringsOffset + keyStringsPoolChunk.chunkEndOffset
            val typeChunk = ResTableTypeSpecAndTypeChunk(
                resourceByteArray,
                parentOffset,
                globalStringList = stringPoolChunk.resStringPoolRefOffset.globalStringList,
                typeStringList = typeStringPoolChunk.resStringPoolRefOffset.globalStringList,
                keyStringList = keyStringsPoolChunk.resStringPoolRefOffset.globalStringList,
                packageId = tablePackageChunk.id
            )
            logBeginningTitle(6, "Type spec and Type", parentOffset, typeChunk)


        } ?: run {
            Logger.error("The resource data is null, there is something wrong.")
        }
    }

    private fun logBeginningTitle(
        index: Int,
        name: String,
        parentOffset: Int,
        chunk: ChunkParseOperator
    ) {
        Logger.debug("\n${Logger.TITLE_TAG_START} begin to read $index chunk: $name, parentOffset is $parentOffset ${Logger.TITLE_TAG_END}")
        Logger.debug(chunk.toString())
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