package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.package3.ResTablePackageHeaderThirdChunk
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolSecondChunk
import com.wj.parse.androidresource.entity.stringpool5.ResKeyStringsPoolFiveChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.interfaces.ChunkType
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils

/**
 * create by wenjing.liu at 2023/7/29
 */
class ResTableTypeSpecAndTypeSixChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderFirstChunk.chunkEndOffset] + [ResStringPoolSecondChunk.chunkEndOffset] + [ResTablePackageHeaderThirdChunk.keyStrings] + [ResKeyStringsPoolFiveChunk.chunkEndOffset]
     */
    override val startOffset: Int,
    /**
     * all resource type list: [attr, drawable, layout, anim, raw, color, dimen, string, style, id]
     */
    val typeStringList: MutableList<String> = mutableListOf()
) : ChunkParseOperator {
   private val typeChunks = mutableListOf<ChunkParseOperator>()
    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header because this chunk has many childs")
            null
        }

    /**
     * TODO this is wrong, should consider
     */
    override val chunkEndOffset: Int
        get() = TODO()

    override val position: Int
        get() = 6

    override val childPosition: Int
        get() = 0

    override fun chunkParseOperator(): ChunkParseOperator {
        var endOffset = startOffset
        val sourceSize = inputResourceByteArray.size
        var childByteArray: ByteArray? = resArrayStartZeroOffset
        // loop every chunk
        var chunkPosition = 1
        while (!isTypeChunkParsingCompleted(endOffset = endOffset, sourceSize = sourceSize)) {
            childByteArray?.let { child ->
                val childHeader = ResChunkHeader(child)
                // Logger.debug("sourceSize is $sourceSize, endOffset is $endOffset, header.type is ${childHeader.type}")
                when (childHeader.type) {
                    ChunkType.RES_TABLE_TYPE_SPEC_TYPE.value -> {
                        val typeChunk = ResTableTypeSpecSixChunk(
                            inputResourceByteArray,
                            endOffset,
                            chunkPosition
                        )
                        typeChunk.startParseChunk().also {
                            typeChunks.add(it)
                            // go to next chunk
                            endOffset += typeChunk.chunkEndOffset
                        }
                    }

                    else -> {
                        val typeChunk =
                            ResTableTypeSixChunk(
                                inputResourceByteArray,
                                endOffset,
                                chunkPosition,
                                typeStringList
                            )
                        typeChunk.startParseChunk().also {
                            typeChunks.add(it)
                            // go to next chunk
                            endOffset += typeChunk.chunkEndOffset
                        }
                    }
                }
            }
            // go to next chunk
            chunkPosition += 1
            childByteArray =
                Utils.copyByte(inputResourceByteArray, endOffset) ?: run {
                    Logger.debug(
                        "\n ^^^^^^^^ \n" +
                                " !Oh, thanks goodness, we has finished to parse all resource type symbol table and resource key symbol table in the ${this.javaClass.simpleName}" +
                                "\n ^^^^^^^^ \n"
                    )
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
    override fun toString(): String = typeChunks.map { chunk ->
        chunk.toString()
    }.joinToString()

    companion object {
        const val SPEC_PUBLIC = 0x40000000
        const val SPEC_STAGED_API = 0x20000000u
        const val ID_BYTE = 1
        const val RES0_BYTE = 1
        const val RES1_BYTE = 2
        const val ENTRY_COUNT_BYTE = 4
        const val ENTRIES_START_BYTE = 4
    }

}
