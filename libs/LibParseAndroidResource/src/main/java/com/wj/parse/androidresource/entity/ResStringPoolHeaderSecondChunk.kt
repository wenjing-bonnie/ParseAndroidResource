package com.wj.parse.androidresource.entity

import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.utils.Utils


/**
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#456
 *
 *  struct ResStringPool_header
 *  {
 *      struct ResChunk_header header;
 *      // Number of strings in this pool (number of uint32_t indices that follow
 *      // in the data).
 *      uint32_t stringCount;
 *      // Number of style span arrays in the pool (number of uint32_t indices
 *      // follow the string indices).
 *      uint32_t styleCount;
 *      // Flags.
 *      enum {
 *      // If set, the string index is sorted by the string values (based
 *      // on strcmp16()).
 *           SORTED_FLAG = 1<<0,
 *      // String pool is encoded in UTF-8
 *          UTF8_FLAG = 1<<8
 *      };
 *      uint32_t flags;
 *      // Index from header of the string data.
 *      uint32_t stringsStart;
 *      // Index from header of the style data.
 *      uint32_t stylesStart;
 *  };
 */
class ResStringPoolHeaderSecondChunk(
    /**
     * The [resourceByteArray] should be associated with [startOffset].
     * <h> Precondition </h>
     *   The [startOffset] of [ResStringPoolHeaderSecondChunk] is [ResourceTableHeaderFirstChunk.chunkEndOffset].
     * <h> Solution </h>
     *  If the [resourceByteArray] starts from [ResourceTableHeaderFirstChunk.chunkEndOffset] you don't require to set [startOffset];
     *  Of course, if you can set [ResourceTableHeaderFirstChunk.chunkEndOffset] to [startOffset] that you require to set [resourceByteArray] to the whole byte array.
     */
    private val resourceByteArray: ByteArray,
    private val startOffset: Int = 0
) : ChunkParseOperator {
    lateinit var header: ResChunkHeader
    var stringCount: Int = 0
    var styleCount: Int = 0
    var flags: Int = 0
    var stringStart: Int = 0
    var stylesStart: Int = 0

    override val chunkEndOffset: Int
        get() = STRING_COUNT_BYTE + STYLE_COUNT_BYTE + FLAGS_BYTE + STRING_START_BYTE + STYLE_START_BYTE

    init {
        chunkParseOperator()
    }

    override fun chunkParseOperator(): ResStringPoolHeaderSecondChunk = run{
        var poolStartOffset = startOffset
        header = ResChunkHeader(resourceByteArray, poolStartOffset)
        // string count
        poolStartOffset += header.chunkEndOffset
        val stringByteArray = Utils.copyByte(
            resourceByteArray,
            poolStartOffset,
            STRING_COUNT_BYTE
        )
        stringCount = Utils.byte2Int(stringByteArray)
        // style count
        poolStartOffset += STYLE_COUNT_BYTE
        val styleCountByteArray = Utils.copyByte(
            resourceByteArray,
            poolStartOffset,
            STYLE_COUNT_BYTE
        )
        styleCount = Utils.byte2Int(styleCountByteArray)
        // flags count
        poolStartOffset += FLAGS_BYTE
        val flagByteArray = Utils.copyByte(
            resourceByteArray,
            poolStartOffset,
            FLAGS_BYTE
        )
        flags = Utils.byte2Int(flagByteArray)
        // string start
        poolStartOffset += STRING_START_BYTE
        val stringStartByteArray = Utils.copyByte(
            resourceByteArray,
            poolStartOffset,
            STRING_START_BYTE
        )
        stringStart = Utils.byte2Int(stringStartByteArray)
        // style start
        poolStartOffset += STYLE_START_BYTE
        val styleStartByteArray = Utils.copyByte(
            resourceByteArray,
            poolStartOffset,
            STYLE_START_BYTE
        )
        stylesStart = Utils.byte2Int(styleStartByteArray)
        this
    }

    override fun toString(): String =
        "Part2: -> Resource String Pool header: $header,\n" +
                "          stringCount is $stringCount, styleCount is $styleCount, flags is ${
                    when (flags) {
                        Flags.UTF8_FLAG.value -> "UTF-8"
                        else -> "strcmp16"
                    }
                }, stringStart is $stringStart, stylesStart is $stylesStart."

    enum class Flags(var value: Int) {
        /** strcmp16() */
        SORTED_FLAG(1 shl 0),

        /** UTF-8 */
        UTF8_FLAG(1 shl 8)
    }

    companion object {
        const val STRING_COUNT_BYTE = 4
        const val STYLE_COUNT_BYTE = 4
        const val FLAGS_BYTE = 4
        const val STRING_START_BYTE = 4
        const val STYLE_START_BYTE = 4
    }
}