package com.wj.parse.androidresource.entity.stringpool2

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolHeaderChunkChild.Companion.OFFSET_BYTE
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils
import java.lang.IllegalStateException
import kotlin.experimental.and

/**
 * This is second child of [ResStringPoolSecondChunk]
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
class ResStringPoolRefChunkChild(
    /**
     * the string pool chunk byte array which index has started from 0 for this child chunk
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
    private val flags: Int,
    private val stringCount: Int,
    private val styleCount: Int,
    private val stringStart: Int,
    private val stylesStart: Int
) : ChunkParseOperator {

    var stringOffsetList = mutableListOf<ResStringPoolRef>()
    var styleOffsetList = mutableListOf<ResStringPoolRef>()
    var stringList = mutableListOf<String>()

    // TODO styleList?????
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
        get() = 2

    override fun chunkProperty(): ChunkProperty =
        ChunkProperty.CHUNK_CHILD

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
         * ... ---- the next string----|-------------------------------- the first string -----------------------------|
         * ... |---------1(B)----------|----3(B)---|------length of this string(B)----|--1(B)--|---------1(B)----------|
         * ... | length of this string |      ?    |   res/layout/activity_main.xml   |        | length of this string |
         * ... |---------1(B)----------|----3(B)---|------length of this string(B)----|--1(B)--|---------1(B)----------|
         *
         */
        childOffset += styleCount * OFFSET_BYTE
        // Logger.error(" \n ===== style childOffset = $childOffset")
        for (index in 0 until stringCount) {
            // 1.read the length of string from first two byte , the last byte is length of this string
            val stringByteArray =
                Utils.copyByte(resArrayStartZeroOffset, childOffset, OFFSET_BYTE / 2)
            stringByteArray?.let {
                val stringLength = (stringByteArray[1] and 0x7F).toInt()
                when {
                    stringLength > 0 -> {
                        val stringByte = Utils.copyByte(
                            resArrayStartZeroOffset,
                            childOffset + OFFSET_BYTE / 2,
                            stringLength
                        ) ?: kotlin.run {
                            throw IllegalStateException("The string byte array is null")
                        }
                        stringList.add(
                            String(
                                stringByte,
                                if (flags == ResStringPoolHeaderChunkChild.Flags.UTF8_FLAG.value)
                                    Charsets.UTF_8
                                else
                                    // TODO strcmp16()
                                    Charsets.UTF_8
                            )
                        )
                    }

                    else -> {
                        stringList.add("")
                    }
                }
                // TODO why is 3
                childOffset += (stringLength + 3)
                // Logger.error(" \n =====$index  childOffset = ${childOffset} , stringList: ${stringList[index]}")
            } ?: run {
                throw IllegalStateException("The string byte array is null")
            }
        }
        //TODO read style list
        return this
    }

    /**
     * TODO toString() need to be optimized
     */
    override fun toString(): String =
        formatToString(
            chunkName = "Resource Pool Ref offset",
            "string offset is $stringOffsetList",
            "string list is $stringList"
        )

    private fun byteOffset(sourceBytes: ByteArray?) = run {
        val buffer = StringBuffer()
        sourceBytes?.forEach {
            buffer.append(it)
            buffer.append(" , ")
        }
        buffer.toString()
    }
}
