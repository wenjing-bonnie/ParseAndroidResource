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

    lateinit var resStringPoolHeader: ResStringPoolHeaderChunkChild
    lateinit var resStringPoolRefOffset: ResStringPoolRefOffsetChunkChild

    override val resArrayStartZeroOffset: ByteArray
        get() = Utils.copyByte(wholeResource, startOffset) ?: kotlin.run {
            Logger.error("he Res String pool has a bad state, the array is null")
            throw IllegalStateException("The Res String pool has a bad state, the array is null")
        }

    /**
     * TODO need all childs of this chunk
     */
    override val chunkEndOffset: Int
        get() = startOffset + resStringPoolHeader.chunkEndOffset + resStringPoolRefOffset.chunkEndOffset

    override fun chunkProperty(): ChunkProperty = ChunkProperty.CHUNK

    init {
        chunkParseOperator()
        checkChunkAttributes()
    }

    override fun chunkParseOperator(): ChunkParseOperator = run {
        // header
        resStringPoolHeader =
            ResStringPoolHeaderChunkChild(resArrayStartZeroOffset)
        var childStartOffsetInParent = resStringPoolHeader.chunkEndOffset
        // string offset and style offset
        resStringPoolRefOffset = ResStringPoolRefOffsetChunkChild(
            resArrayStartZeroOffset,
            startOffset = childStartOffsetInParent,
            stringCount = resStringPoolHeader.stringCount,
            styleCount = resStringPoolHeader.styleCount
        )
        childStartOffsetInParent
        this
    }

    /**
     * TODO need all childs of this chunk
     */
    override fun toString(): String =
        "Part2: -> First child =>=> ${resStringPoolHeader}\n" +
                "          Second child =>=> $resStringPoolRefOffset"

}