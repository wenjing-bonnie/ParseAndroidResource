package com.wj.parse.androidresource.entity.stringpool2

import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty

/**
 * This is the second chunk of resource.arsc file.
 */
class ResStringPoolSecondChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderFirstChunk.chunkEndOffset]
     */
    override val startOffset: Int
) : ChunkParseOperator {

    lateinit var resStringPoolHeader: ResStringPoolHeaderChunkChild
    lateinit var resStringPoolRefOffset: ResStringPoolRefOffsetChunkChild

    /**
     * all childs of this chunk
     */
    override val chunkEndOffset: Int
        get() = startOffset + resStringPoolHeader.chunkEndOffset + resStringPoolRefOffset.chunkEndOffset

    override fun chunkProperty(): ChunkProperty = ChunkProperty.CHUNK

    init {
        chunkParseOperator()
        checkChunkAttributes()
    }

    override fun chunkParseOperator(): ChunkParseOperator {
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
        return this
    }

    /**
     * all childs of this chunk
     */
    override fun toString(): String =
        "Part2: -> First child =>=> ${resStringPoolHeader}\n" +
                "          Second child =>=> $resStringPoolRefOffset" +
                "\nPart2: -> End..."

}