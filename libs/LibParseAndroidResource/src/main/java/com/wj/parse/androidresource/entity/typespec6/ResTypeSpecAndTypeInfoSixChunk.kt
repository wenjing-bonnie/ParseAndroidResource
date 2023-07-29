package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.interfaces.ChunkType
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils

/**
 * create by wenjing.liu at 2023/7/29
 */
class ResTypeSpecAndTypeInfoSixChunk(
    override val inputResourceByteArray: ByteArray,
    override val startOffset: Int
) : ChunkParseOperator {
    lateinit var typeChunk: ChunkParseOperator
    private var toStringBuffer: StringBuffer = StringBuffer()
    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header because this chunk has many childs")
            null
        }

    /**
     * TODO this is wrong, should consider
     */
    override val chunkEndOffset: Int
        get() = typeChunk.chunkEndOffset

    override fun chunkParseOperator(): ChunkParseOperator {
        var endOffset = startOffset
        val sourceSize = inputResourceByteArray.size
        var childByteArray: ByteArray? = resArrayStartZeroOffset
        // loop every chunk
        while (!isTypeChunkParsingCompleted(endOffset = endOffset, sourceSize = sourceSize)) {
            childByteArray?.let { child ->
                val childHeader = ResChunkHeader(child)
                //
                // Logger.debug("sourceSize is $sourceSize, endOffset is $endOffset, header.type is ${childHeader.type}")
                when (childHeader.type) {
                    ChunkType.RES_TABLE_TYPE_SPEC_TYPE.value -> {
                        typeChunk = ResTypeSpecSixChunk(
                            inputResourceByteArray,
                            endOffset
                        )
                        typeChunk.startParseChunk().also {
                            toStringBuffer.append(it)
                            toStringBuffer.append("\n")
                        }
                    }

                    else -> {
                        typeChunk = ResTypeInfoSixChunk(inputResourceByteArray, endOffset)
                        typeChunk.startParseChunk().also {
                            toStringBuffer.append(it)
                            toStringBuffer.append("\n")
                        }
                    }
                }
            }

            // go to next chunk
            endOffset += typeChunk.chunkEndOffset
            childByteArray =
                Utils.copyByte(inputResourceByteArray, endOffset) ?: run {
                    Logger.debug("has read all byte array")
                    null
                }
        }
        return this
    }

    private fun isTypeChunkParsingCompleted(endOffset: Int, sourceSize: Int): Boolean =
        endOffset >= sourceSize

    override fun chunkProperty() = ChunkProperty.CHUNK

    /***
     * TODO optimise
     */
    override fun toString(): String = when {
        ::typeChunk.isInitialized -> "$toStringBuffer"
        else -> "Not isInitialized"
    }

    companion object {
        const val SPEC_PUBLIC = 0x40000000
        const val ID_BYTE = 1
        const val RES0_BYTE = 1
        const val RES1_BYTE = 2
        const val ENTRY_COUNT_BYTE = 4
        const val ENTRIES_START_BYTE = 4
    }

}