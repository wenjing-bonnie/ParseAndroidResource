package com.wj.parse.androidresource.entity

import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.interfaces.ChunkType
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils


/**
 * Every resource.arsc file has a series of chunks, header that appears at the front of every data chunk.
 * 1. the identifier for this chunk
 * 2. the size of this chunk header
 * 3. total size of this chunk
 *
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#213
 * struct ResChunk_header
 *  {
 *    // Type identifier for this chunk.  The meaning of this value depends
 *    // on the containing chunk.
 *    uint16_t type;

 *    // Size of the chunk header (in bytes).  Adding this value to
 *    // the address of the chunk allows you to find its associated data (if any).
 *    uint16_t headerSize;

 *    // Total size of this chunk (in bytes).  This is the chunkSize plus
 *    // the size of any data associated with the chunk.  Adding this value
 *    // to the chunk allows you to completely skip its contents (including
 *    // any child chunks).  If this value is the same as chunkSize, there is
 *    // no data associated with the chunk.
 *    uint32_t size;
 *  };
 */
class ResChunkHeader(
    /**
     * The [resArrayStartZeroOffset] has been changed in every chunk and been started from the first byte.
     */
    override val resArrayStartZeroOffset: ByteArray
) : ChunkParseOperator {

    /**
     * first 2 Byte is type
     *    // Type identifier for this chunk.  The meaning of this value depends
     *    // on the containing chunk.
     *    uint16_t type;
     */
    var type: Short = 0

    /**
     * the next 2 Byte is header size
     *    // Size of the chunk header (in bytes).  Adding this value to
     *    // the address of the chunk allows you to find its associated data (if any).
     *    uint16_t headerSize;
     */
    var headerSize: Short = 0

    /**
     * the next 4 Byte is size
     *    // Total size of this chunk (in bytes).  This is the chunkSize plus
     *    // the size of any data associated with the chunk.  Adding this value
     *    // to the chunk allows you to completely skip its contents (including
     *    // any child chunks).  If this value is the same as chunkSize, there is
     *    // no data associated with the chunk.
     *    uint32_t size;
     */
    var size: Int = 0

    /**
     * first 2 Byte is type, the next 2 Byte is header size and the next 4 Byte is size
     *  ｜ 2 Byte is type ｜2 Byte is header size｜ 4 Byte is size｜ = 8 Byte
     * This is a child of chunk, so this size should return the size of this child
     */
    override val chunkEndOffset: Int
        get() = TYPE_BYTE + HEADER_SIZE_BYTE + SIZE_BYTE

    override val position: Int
        get() = 0

    override val childPosition: Int
        get() = 0

    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header because this chunk is header chunk.")
            null
        }

    init {
        chunkParseOperator()
    }

    /**
     * read from the first byte
     */
    override val startOffset: Int
        get() = 0

    /**
     * will not be used. The [resArrayStartZeroOffset] has been changed in every chunk and been started from the first byte.
     */
    override val inputResourceByteArray: ByteArray
        get() = resArrayStartZeroOffset

    override fun chunkParseOperator(): ResChunkHeader {
        // read type
        var attributeStartOffset = startOffset
        var attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeStartOffset, TYPE_BYTE)
        type = Utils.byte2Short(attributeByteArray)

        // read header size
        attributeStartOffset += TYPE_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeStartOffset, HEADER_SIZE_BYTE)
        headerSize = Utils.byte2Short(attributeByteArray)

        // read size
        attributeStartOffset += HEADER_SIZE_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeStartOffset, SIZE_BYTE)
        size = Utils.byte2Int(attributeByteArray)
        return this
    }

    /**
     * 1B = 8bit;
     * 1KB = 1024B
     * 1MB = 1024KB
     */
    override fun toString(): String =
        "Chunk header: {type is ${ChunkType.valueOf(type)}, header size is $headerSize, size is ${size}bit (about ${(size / 1024.0)}B)}"

    override fun chunkProperty(): ChunkProperty =
        ChunkProperty.COMMON_HEADER

    companion object {
        const val TYPE_BYTE = 2
        const val HEADER_SIZE_BYTE = 2
        const val SIZE_BYTE = 4
    }
}
