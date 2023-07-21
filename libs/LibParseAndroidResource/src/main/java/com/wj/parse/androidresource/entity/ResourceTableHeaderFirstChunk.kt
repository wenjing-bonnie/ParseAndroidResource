package com.wj.parse.androidresource.entity

import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.utils.Utils

/**
 * the first part of resource.arsc file.
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
     *  The startOffset of [ResourceTableHeaderFirstChunk] is 0, so the [resourceByteArray] should be the whole byte array
     */
    private val resourceByteArray: ByteArray
) : ChunkParseOperator {

    /**
     * the first [ResChunkHeader#getChunkByteSize()] Byte is header
     */
    lateinit var header: ResChunkHeader

    /**
     * the next 4 Byte is package count
     */
    var packageCount: Int = 0

    override val chunkEndOffset: Int
        get() = header.chunkEndOffset + TABLE_HEADER_BYTE

    init {
        chunkParseOperator()
    }

    override fun chunkParseOperator(): ResourceTableHeaderFirstChunk = run {
        header = ResChunkHeader(resourceByteArray, START_OFFSET)
        val packageCountByteArray = Utils.copyByte(
            resourceByteArray,
            header.chunkEndOffset,
            TABLE_HEADER_BYTE
        )
        packageCount = Utils.byte2Int(packageCountByteArray)
        this
    }

    override fun toString(): String =
        "Part1: -> Resource Table header: $header,\n" +
                "         packageCount is $packageCount."

    companion object {
        const val TABLE_HEADER_BYTE = 4
        const val START_OFFSET = 0
    }
}