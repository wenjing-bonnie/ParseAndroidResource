package com.wj.parse.androidresource.entity.stringpool2

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils


/**
 * This is first child of [ResStringPoolSecondChunk]
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
class ResStringPoolHeaderSecondChunkChild(
    /**
     * the whole byte array.
     */
    private val wholeResource: ByteArray
) : ChunkParseOperator {
    lateinit var header: ResChunkHeader
    var stringCount: Int = 0
    var styleCount: Int = 0
    var flags: Int = 0
    var stringStart: Int = 0
    var stylesStart: Int = 0

    /**
     * this is child of [ResStringPoolSecondChunk], so it returns the size of this child chunk
     */
    override val chunkEndOffset: Int
        get() = header.chunkEndOffset + STRING_COUNT_BYTE + STYLE_COUNT_BYTE + FLAGS_BYTE + STRING_START_BYTE + STYLE_START_BYTE

    override val resArrayStartZeroOffset: ByteArray
        get() = Utils.copyByte(wholeResource, startOffset) ?: run {
            Logger.error("The header hasn't been initialized, please check.")
            throw IllegalCallerException("The header hasn't been initialized, please check.")
        }

    /**
     * this is part of [ResStringPoolSecondChunk], so it returns 0
     */
    override val startOffset: Int
        get() = 0

    override fun chunkProperty(): ChunkProperty =
        ChunkProperty.CHUNK_CHILD

    init {
        chunkParseOperator()
        checkChunkAttributes()
    }

    override fun chunkParseOperator(): ResStringPoolHeaderSecondChunkChild = run {
        var poolStartOffset = startOffset
        header = ResChunkHeader(resArrayStartZeroOffset)
        // string count
        poolStartOffset += header.chunkEndOffset
        val stringByteArray = Utils.copyByte(
            resArrayStartZeroOffset,
            poolStartOffset,
            STRING_COUNT_BYTE
        )
        stringCount = Utils.byte2Int(stringByteArray)
        // style count
        poolStartOffset += STYLE_COUNT_BYTE
        val styleCountByteArray = Utils.copyByte(
            resArrayStartZeroOffset,
            poolStartOffset,
            STYLE_COUNT_BYTE
        )
        styleCount = Utils.byte2Int(styleCountByteArray)
        // flags count
        poolStartOffset += FLAGS_BYTE
        val flagByteArray = Utils.copyByte(
            resArrayStartZeroOffset,
            poolStartOffset,
            FLAGS_BYTE
        )
        flags = Utils.byte2Int(flagByteArray)
        // string start
        poolStartOffset += STRING_START_BYTE
        val stringStartByteArray = Utils.copyByte(
            resArrayStartZeroOffset,
            poolStartOffset,
            STRING_START_BYTE
        )
        stringStart = Utils.byte2Int(stringStartByteArray)
        // style start
        poolStartOffset += STYLE_START_BYTE
        val styleStartByteArray = Utils.copyByte(
            resArrayStartZeroOffset,
            poolStartOffset,
            STYLE_START_BYTE
        )
        stylesStart = Utils.byte2Int(styleStartByteArray)
        this
    }

    override fun toString(): String =
        "Resource String Pool header: $header,\n" +
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