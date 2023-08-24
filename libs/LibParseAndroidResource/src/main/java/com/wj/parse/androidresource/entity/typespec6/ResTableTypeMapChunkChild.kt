package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger

/**
 * create by wenjing.liu at 2023/8/25
 */
class ResTableTypeMapChunkChild(
    /**
     * the string pool chunk byte array which index has started from 0 for this child chunk
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
) : ChunkParseOperator {
    lateinit var name: ResTableRef
    lateinit var value: ResTableTypeMapChunkChild

    override val position: Int
        get() = ResTableTypeSpecAndTypeSixChunk.POSITION

    override val childPosition: Int
        get() = ChunkParseOperator.CHILD_CHILD_POSITION

    override fun chunkProperty(): ChunkProperty = ChunkProperty.CHUNK_CHILD_CHILD

    override val chunkEndOffset: Int
        get() = TODO("Not yet implemented")

    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header, because this is a child chunk without header.")
            null
        }

    override fun chunkParseOperator(): ChunkParseOperator {
        return this
    }

    override fun toString() = formatToString(
        chunkName = "Res Table Entry Map"
    )

    /**
     * struct ResTable_ref
     * {
     *    uint32_t ident;
     * };
     */
    data class ResTableRef(val ident: Int) {
        val size: Int get() = 4
    }
}