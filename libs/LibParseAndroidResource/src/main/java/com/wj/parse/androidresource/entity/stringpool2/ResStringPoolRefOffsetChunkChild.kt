package com.wj.parse.androidresource.entity.stringpool2

import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolHeaderChunkChild.Companion.OFFSET_BYTE
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils
import java.lang.IllegalStateException

/**
 * This is second child of [ResStringPoolSecondChunk]
 * Reference to a string in a string pool
 *
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#425
 *  TODO rename and find struct
 * * struct ResStringPool_ref
 *  {
 *  // Index into the string pool table (uint32_t-offset from the indices
 *  // immediately after ResStringPool_header) at which to find the location
 *  // of the string data in the pool.
 *     uint32_t index;
 *  };
 *
 */
class ResStringPoolRefOffsetChunkChild(
    /**
     * the string pool chunk byte array which index has started from 0
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
    private val stringCount: Int,
    private val styleCount: Int
) : ChunkParseOperator {

    var stringOffsetList = mutableListOf<Int>()
    var styleOffsetList = mutableListOf<Int>()

    override val chunkEndOffset: Int
        get() = stringCount * OFFSET_BYTE + styleCount * OFFSET_BYTE

    override fun chunkProperty(): ChunkProperty =
        ChunkProperty.CHUNK_OTHER_CHILD

    init {
        chunkParseOperator()
        checkChunkAttributes()
    }

    override fun chunkParseOperator(): ChunkParseOperator = run {
        // read string offset
        var childOffset = 0
        for (index in 0 until stringCount) {
            val sourceBytes: ByteArray? =
                Utils.copyByte(
                    resArrayStartZeroOffset,
                    childOffset + index * OFFSET_BYTE,
                    OFFSET_BYTE
                )
            sourceBytes?.let {
                stringOffsetList.add(Utils.byte2Int(it))
//                Logger.debug("The $index byte array is ${byteOffset(it)},  string offset is ${stringOffsetList[index]} ")
            } ?: run {
                Logger.error("Read string offset is null")
                throw IllegalStateException("Read string offset is null")
            }
        }
        // read style offset
        //  TODO need to test it
        childOffset = stringCount * OFFSET_BYTE
        for (index in 0 until styleCount) {
            val sourceBytes: ByteArray? =
                Utils.copyByte(
                    resArrayStartZeroOffset,
                    childOffset + index * OFFSET_BYTE,
                    OFFSET_BYTE
                )
            sourceBytes?.let {
                styleOffsetList.add(Utils.byte2Int(it))
                Logger.debug("The $index byte array is ${byteOffset(it)},  style offset is ${styleOffsetList[index]} ")
            } ?: run {
                Logger.error("Read style offset is null")
                throw IllegalStateException("Read style offset is null")
            }
        }
        this
    }

    override fun toString(): String =
        "Resource Pool Ref offset: string offset is $stringOffsetList, \n          style offset is $styleOffsetList"

    private fun byteOffset(sourceBytes: ByteArray?) = run {
        val buffer = StringBuffer()
        sourceBytes?.forEach {
            buffer.append(it)
            buffer.append(" , ")
        }
        buffer.toString()
    }
}