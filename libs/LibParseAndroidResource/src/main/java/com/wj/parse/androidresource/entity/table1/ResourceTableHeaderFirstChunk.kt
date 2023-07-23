package com.wj.parse.androidresource.entity.table1

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils

/**
 * the first chunk of resource.arsc file.
 * This part describes the size and package number of this file.
 *
 *  https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#885
 * * struct ResTable_header
 *  {
 *    struct ResChunk_header header;

 *     // The number of ResTable_package structures. an APK may include several package resources
 *     int32_t packageCount;
 *  };
 */
class ResourceTableHeaderFirstChunk(
    /**
     * The whole byte array.
     * The [startOffset] of this chunk is 0 and [resArrayStartZeroOffset] is the whole array.
     */
    override val inputResourceByteArray: ByteArray
) : ChunkParseOperator {

    /**
     * the first [ResChunkHeader#getChunkByteSize()] Byte is header
     */
    lateinit var header: ResChunkHeader

    /**
     * the next 4 Byte is package count
     */
    var packageCount: Int = 0

    override fun chunkProperty(): ChunkProperty =
        ChunkProperty.CHUNK

    override val chunkEndOffset: Int
        get() = if (::header.isInitialized) {
            header.chunkEndOffset + TABLE_HEADER_BYTE
        } else {
            Logger.error("The header hasn't been initialized, please check.")
            throw IllegalCallerException("The header hasn't been initialized, please check.")
        }

    /**
     * The startOffset of this chunk is 0
     */
    override val startOffset: Int
        get() = 0

    init {
        chunkParseOperator()
        checkChunkAttributes()
    }

    override fun chunkParseOperator(): ResourceTableHeaderFirstChunk = run {
        header = ResChunkHeader(resArrayStartZeroOffset)
        val packageCountByteArray = Utils.copyByte(
            resArrayStartZeroOffset,
            header.chunkEndOffset,
            TABLE_HEADER_BYTE
        )
        packageCount = Utils.byte2Int(packageCountByteArray)
        this
    }

    override fun toString(): String =
        "Part1: -> Resource Table header: $header,\n" +
                "         packageCount is $packageCount." +
                "\nPart1: -> End..."

    companion object {
        const val TABLE_HEADER_BYTE = 4
    }
}