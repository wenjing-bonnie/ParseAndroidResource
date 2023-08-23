package com.wj.parse.androidresource.entity.stringpool2

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger

/**
 * This is the second chunk of resource.arsc file.
 */
open class ResStringPoolSecondChunk(
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
    lateinit var resStringPoolRefOffset: ResStringPoolRefChunkChild

    /**
     * The end offset byte of this chunk
     */
    override val chunkEndOffset: Int
        get() = when {
            ::resStringPoolHeader.isInitialized -> resStringPoolHeader.header.size
            else -> (startOffset + resStringPoolHeader.chunkEndOffset + resStringPoolRefOffset.chunkEndOffset)
        }

    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header because this chunk has two childs")
            null
        }

    override val position: Int
        get() = 2

    override val childPosition: Int
        get() = 0

    override fun chunkProperty(): ChunkProperty = ChunkProperty.CHUNK

    override fun chunkParseOperator(): ChunkParseOperator {
        // header
        resStringPoolHeader =
            ResStringPoolHeaderChunkChild(resArrayStartZeroOffset)
        var childStartOffsetInParent = resStringPoolHeader.chunkEndOffset
        // string offset and style offset
        resStringPoolRefOffset = ResStringPoolRefChunkChild(
            resArrayStartZeroOffset,
            startOffset = childStartOffsetInParent,
            flags = resStringPoolHeader.flags,
            stringCount = resStringPoolHeader.stringCount,
            styleCount = resStringPoolHeader.styleCount,
            stringStart = resStringPoolHeader.stringStart,
            stylesStart = resStringPoolHeader.stylesStart
        )
        return this
    }

    /**
     * all childs of this chunk
     */
    override fun toString(): String =
        formatToString(
            chunkName = "Resource String Pool",
            resStringPoolHeader.toString(),
            resStringPoolRefOffset.toString()
        )

}