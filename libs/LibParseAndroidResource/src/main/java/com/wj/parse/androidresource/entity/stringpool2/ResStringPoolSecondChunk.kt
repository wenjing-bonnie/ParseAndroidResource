package com.wj.parse.androidresource.entity.stringpool2

import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils
import java.lang.IllegalStateException

/**
 * This is the second chunk of resource.arsc file.
 */
class ResStringPoolSecondChunk(
    /**
     * whole resource byte array
     */
    private val wholeResource: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderFirstChunk.chunkEndOffset]
     */
    override val startOffset: Int
) : ChunkParseOperator {

    lateinit var resStringPoolHeaderSecondChunkChild: ResStringPoolHeaderSecondChunkChild
    lateinit var resStringPoolRefOffsetSecondChunkChild: ResStringPoolRefOffsetSecondChunkChild

    override val resArrayStartZeroOffset: ByteArray
        get() = Utils.copyByte(wholeResource, startOffset) ?: kotlin.run {
            Logger.error("he Res String pool has a bad state, the array is null")
            throw IllegalStateException("The Res String pool has a bad state, the array is null")
        }

    /**
     * TODO need all childs of this chunk
     */
    override val chunkEndOffset: Int
        get() = startOffset + resStringPoolHeaderSecondChunkChild.chunkEndOffset + resStringPoolRefOffsetSecondChunkChild.chunkEndOffset

    override fun chunkProperty(): ChunkProperty = ChunkProperty.CHUNK

    init {
        chunkParseOperator()
        checkChunkAttributes()
    }

    override fun chunkParseOperator(): ChunkParseOperator = run {
        resStringPoolHeaderSecondChunkChild =
            ResStringPoolHeaderSecondChunkChild(resArrayStartZeroOffset)
        val startOffset = startOffset + resStringPoolHeaderSecondChunkChild.chunkEndOffset
        // resStringPoolRefOffsetSecondChunk =
        this
    }

    /**
     * TODO need all childs of this chunk
     */
    override fun toString(): String =
        "Part2: -> Resource String Pool Header : ${resStringPoolHeaderSecondChunkChild},\n"

}