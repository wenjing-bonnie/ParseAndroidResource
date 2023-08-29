package com.wj.parse.androidresource.entity.stringpool2

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolChunk.Companion.CHILD_ARRAY_POSITION
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolHeaderChunk.Companion.OFFSET_BYTE
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils
import java.lang.IllegalStateException
import kotlin.experimental.and

/**
 * This is second child of [ResGlobalStringPoolChunk]
 *
 * Reference to a string in a string pool
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#425
 *
 * * struct ResStringPool_ref
 *  {
 *  // Index into the string pool table (uint32_t-offset from the indices
 *  // immediately after ResStringPool_header) at which to find the location
 *  // of the string data in the pool.
 *     uint32_t index;
 *  };
 *
 */
class ResGlobalStringPoolRefChildChunk(
    /**
     * the string pool chunk byte array which index has started from 0 for this child chunk
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
    private val headerSize: Short,
    private val flags: Int,
    private val stringCount: Int,
    private val styleCount: Int,
    /**
     * Index from header of the string data.
     * It means the index contains header.headerSize
     */
    private val stringStart: Int,
    private val stylesStart: Int
) : ChunkParseOperator {
    /**
     * It is the beginning index of string byte array.
     * one string byte array is like this:
     *       stringOffsetList[1] ->|                                                         stringOffsetList[0] ->|
     * ... ---- the next string----|-------------------------------- the first string -----------------------------|
     * ... |---------1(B)----------|----3(B)---|------length of this string(B)----|---------1(B)----------|--1(B)--|
     * ... | length of this string |    null   |   res/layout/activity_main.xml   | length of this string |        |
     * ... |---------1(B)----------|----3(B)---|------length of this string(B)----|---------1(B)----------|--1(B)--|
     */
    var stringOffsetList = mutableListOf<ResStringPoolRef>()
    var styleOffsetList = mutableListOf<ResStringPoolRef>()
    var globalStringList = mutableListOf<String>()

    /**
     * TODO implement
     * /**
     * This structure defines a span of style information associated with
     * a string in the pool.
     *  */
     *  struct ResStringPool_span
     * {
     *       enum {
     *       END = 0xFFFFFFFF
     *   };
     *   // This is the name of the span -- that is, the name of the XML
     * // tag that defined it.  The special value END (0xFFFFFFFF) indicates
     * // the end of an array of spans.
     * ResStringPool_ref name;
     * // The range of characters in the string that this span applies to.
     * uint32_t firstChar, lastChar;
     * };
     */
    var styleList = mutableListOf<String>()


    // read string offset
    var childOffset = 0

    override val chunkEndOffset: Int
        get() = childOffset
    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header because this chunk is a child chunk.")
            null
        }

    override val position: Int
        get() = 2

    override val childPosition: Int
        get() = CHILD_ARRAY_POSITION

    override val chunkProperty
        get() = ChunkProperty.CHUNK_AREA_CHILD_CHILD

    init {
        checkChunkAttributes()
        chunkParseOperator()
    }

    override fun chunkParseOperator(): ChunkParseOperator {
        // read string offset list
        for (index in 0 until stringCount) {
            val sourceBytes: ByteArray? =
                Utils.copyByte(
                    resArrayStartZeroOffset,
                    childOffset + index * OFFSET_BYTE,
                    OFFSET_BYTE
                )
            sourceBytes?.let {
                stringOffsetList.add(ResStringPoolRef(Utils.byte2Int(it)))
//                Logger.debug("The $index byte array is ${byteOffset(it)},  string offset is ${stringOffsetList[index]} ")
            } ?: run {
                Logger.error("Read string offset is null")
                throw IllegalStateException("Read string offset is null")
            }
        }
        // read style offset list
        //  TODO need to test it
        childOffset += stringCount * OFFSET_BYTE
        // Logger.debug("1 childOffset = $childOffset")
        for (index in 0 until styleCount) {
            val sourceBytes: ByteArray? =
                Utils.copyByte(
                    resArrayStartZeroOffset,
                    childOffset + index * OFFSET_BYTE,
                    OFFSET_BYTE
                )
            sourceBytes?.let {
                styleOffsetList.add(ResStringPoolRef(Utils.byte2Int(it)))
                Logger.debug("The $index byte array is ${byteOffset(it)},  style offset is ${styleOffsetList[index]} ")
            } ?: run {
                Logger.error("Read style offset is null")
                throw IllegalStateException("Read style offset is null")
            }
        }
        // read string list
        /** one string in the chunk is
         * 1. read the length of string from first two byte , the last byte is length of this string
         *       stringOffsetList[1] ->|                                                         stringOffsetList[0] ->|
         * ... ---- the next string----|-------------------------------- the first string -----------------------------|
         * ... |---------1(B)----------|----3(B)---|------length of this string(B)----|---------1(B)----------|--1(B)--|
         * ... | length of this string |    null   |   res/layout/activity_main.xml   | length of this string |        |
         * ... |---------1(B)----------|----3(B)---|------length of this string(B)----|---------1(B)----------|--1(B)--|
         *
         */
        childOffset += styleCount * OFFSET_BYTE
        // Logger.error(" \n ===== style childOffset = $childOffset")
        // stringListByChildOffset()
        stringListByStringOffset()
        //TODO read style list
        return this
    }

    private fun stringListByStringOffset() {
        stringOffsetList.forEach { ref ->
            // 1.read the length of string from first two byte , the last byte is length of this string
            // stringStart contains headerSize, but resArrayStartZeroOffset is without header's data when we pass it from [ResStringPoolSecondChunk]
            childOffset = stringStart - headerSize + ref.index
            // Logger.error("  ===== style childOffset = $childOffset")
            val stringLengthArray =
                Utils.copyByte(resArrayStartZeroOffset, childOffset, OFFSET_BYTE / 2)
            val stringLength = stringLengthArray?.let {
                (stringLengthArray[1] and 0x7F).toInt()
            } ?: 0
            if (stringLength <= 0) {
                return@forEach
            }

            Utils.copyByte(resArrayStartZeroOffset, childOffset + OFFSET_BYTE / 2, stringLength)
                ?.let {
                    globalStringList.add(
                        String(
                            it,
                            if (flags == ResStringPoolHeaderChunk.Flags.UTF8_FLAG.value)
                                Charsets.UTF_8
                            else
                            // TODO strcmp16()
                                Charsets.UTF_8
                        )
                    )
                }
        }
    }

    /**
     * TODO toString() need to be optimized
     */
    override fun toString(): String =
        formatToString(
            chunkName = "Resource Pool Ref offset",
            "string offset is ${
                stringOffsetList.joinToString(
                    prefix = "[",
                    limit = 10,
                    truncated = "...",
                    postfix = "]"
                )
            }",
            "string list is ${
                globalStringList.joinToString(
                    prefix = "[",
                    limit = 10,
                    truncated = "...",
                    postfix = "]"
                )
            }"
        )

    private fun byteOffset(sourceBytes: ByteArray?) = run {
        val buffer = StringBuffer()
        sourceBytes?.forEach {
            buffer.append(it)
            buffer.append(" , ")
        }
        buffer.toString()
    }

    /**
     * @Deprecated
     * we can use [ResGlobalStringPoolRefChildChunk.stringOffsetList] to get the string list
     * so this method has been replaced by [ResGlobalStringPoolRefChildChunk.stringListByStringOffset]
     */
    private fun stringListByChildOffset() {
        for (index in 0 until stringCount) {
            // 1.read the length of string from first two byte , the last byte is length of this string
            val stringByteArray =
                Utils.copyByte(resArrayStartZeroOffset, childOffset, OFFSET_BYTE / 2)
            stringByteArray?.let {
                val stringLength = (stringByteArray[1] and 0x7F).toInt()
                // Logger.error(" ===== stringLength = $stringLength, childOffset = ${childOffset}")
                when {
                    stringLength > 0 -> {
                        val stringByte = Utils.copyByte(
                            resArrayStartZeroOffset,
                            childOffset + OFFSET_BYTE / 2,
                            stringLength
                        ) ?: kotlin.run {
                            throw IllegalStateException("The string byte array is null")
                        }
                        globalStringList.add(
                            String(
                                stringByte,
                                if (flags == ResStringPoolHeaderChunk.Flags.UTF8_FLAG.value)
                                    Charsets.UTF_8
                                else
                                // TODO strcmp16()
                                    Charsets.UTF_8
                            )
                        )
                    }

                    else -> {
                        globalStringList.add("")
                    }
                }
                // there is 3 byte is null after the string byte
                childOffset += (stringLength + 3)
                // Logger.error(" \n =====$index  childOffset = ${childOffset} , stringList: ${stringList[index]}")
            } ?: run {
                throw IllegalStateException("The string byte array is null")
            }
        }
    }

}
