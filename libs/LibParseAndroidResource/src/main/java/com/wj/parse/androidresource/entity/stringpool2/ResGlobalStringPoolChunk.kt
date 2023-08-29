package com.wj.parse.androidresource.entity.stringpool2

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger

/**
 * This is the second chunk of resource.arsc file.
 */
open class ResGlobalStringPoolChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderChunk.endOffset]
     */
    override val startOffset: Int
) : ChunkParseOperator {

    lateinit var resStringPoolHeader: ResStringPoolHeaderChunk
    lateinit var resStringPoolRefOffset: ResGlobalStringPoolRefChildChunk

    /**
     * The end offset byte of this chunk
     */
    override val endOffset: Int
        get() = startOffset + resStringPoolHeader.header.size

    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header because this chunk has two childs")
            null
        }

    override val position: Int
        get() = 2

    override val childPosition: Int
        get() = 0

    override val chunkProperty
        get() = ChunkProperty.CHUNK_AREA_REUSED

    override fun chunkParseOperator(): ChunkParseOperator {
        // header
        resStringPoolHeader =
            ResStringPoolHeaderChunk(resArrayStartZeroOffset)
        var childStartOffsetInParent = resStringPoolHeader.endOffset
        // string offset and style offset
        resStringPoolRefOffset = ResGlobalStringPoolRefChildChunk(
            resArrayStartZeroOffset,
            startOffset = childStartOffsetInParent,
            headerSize = resStringPoolHeader.header.headerSize,
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
            chunkName = "Resource Global String Pool",
            resStringPoolHeader.toString(),
            resStringPoolRefOffset.toString()
        )

    companion object {
        const val CHILD_HEADER_POSITION = 1
        const val CHILD_ARRAY_POSITION = 2
    }

}