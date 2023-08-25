package com.wj.parse.androidresource.entity.table1

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
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
     * the next 4 Byte is package count
     */
    var packageCount: Int = 0

    /**
     * the first [ResChunkHeader#getChunkByteSize()] Byte is header
     */
    override val header: ResChunkHeader
        get() = ResChunkHeader(resArrayStartZeroOffset)

    override val position: Int
        get() = 1

    override val childPosition: Int
        get() = 0

    override val chunkProperty
        get() = ChunkProperty.CHUNK

    override val chunkEndOffset: Int
        get() = header.chunkEndOffset + TABLE_HEADER_BYTE

    /**
     * The startOffset of this chunk is 0
     */
    override val startOffset: Int
        get() = 0

    override fun chunkParseOperator(): ResourceTableHeaderFirstChunk {
        val packageCountByteArray = Utils.copyByte(
            resArrayStartZeroOffset,
            header.chunkEndOffset,
            TABLE_HEADER_BYTE
        )
        packageCount = Utils.byte2Int(packageCountByteArray)
        return this
    }

    override fun toString(): String =
        formatToString(
            chunkName = "Resource Table header",
            "$header",
            "packageCount is $packageCount."
        )

    companion object {
        const val TABLE_HEADER_BYTE = 4
    }
}