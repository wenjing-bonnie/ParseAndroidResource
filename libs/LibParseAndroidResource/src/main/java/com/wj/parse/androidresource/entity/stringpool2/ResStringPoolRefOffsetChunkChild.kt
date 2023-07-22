package com.wj.parse.androidresource.entity.stringpool2

import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty

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
     * The [resArrayStartZeroOffset] should be associated with [startOffset].
     * <h> Precondition </h>
     *   The [startOffset] of [ResStringPoolHeaderChunkChild] is [ResourceTableHeaderFirstChunk.chunkEndOffset].
     * <h> Solution </h>
     *  If the [resArrayStartZeroOffset] starts from [ResourceTableHeaderFirstChunk.chunkEndOffset] you don't require to set [startOffset];
     *  Of course, if you can set [ResourceTableHeaderFirstChunk.chunkEndOffset] to [startOffset] that you require to set [resArrayStartZeroOffset] to the whole byte array.
     */
    private val wholeResource: ByteArray
) : ChunkParseOperator {

    var stringIndexes = mutableListOf<Int>()
    var styleIndexes = mutableListOf<Int>()

    override val chunkEndOffset: Int
        get() = TODO("Not yet implemented")

    override val startOffset: Int
        get() = 0

    override val resArrayStartZeroOffset: ByteArray
        get() = TODO("Not yet implemented")

    override fun chunkProperty(): ChunkProperty =
        ChunkProperty.CHUNK_CHILD

    override fun chunkParseOperator(): ChunkParseOperator = run {

        this
    }

    init {
        chunkParseOperator()
        checkChunkAttributes()
    }

    override fun toString(): String {
        return super.toString()
    }
}