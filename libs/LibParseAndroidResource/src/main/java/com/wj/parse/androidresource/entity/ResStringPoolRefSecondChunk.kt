package com.wj.parse.androidresource.entity

import com.wj.parse.androidresource.interfaces.ChunkParseOperator

/**
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
class ResStringPoolRefSecondChunk(
    /**
     * The [resourceByteArray] should be associated with [startOffset].
     * <h> Precondition </h>
     *   The [startOffset] of [ResStringPoolHeaderSecondChunk] is [ResourceTableHeaderFirstChunk.chunkEndOffset].
     * <h> Solution </h>
     *  If the [resourceByteArray] starts from [ResourceTableHeaderFirstChunk.chunkEndOffset] you don't require to set [startOffset];
     *  Of course, if you can set [ResourceTableHeaderFirstChunk.chunkEndOffset] to [startOffset] that you require to set [resourceByteArray] to the whole byte array.
     */
    private val resourceByteArray: ByteArray,
    private val startOffset: Int = 0
) : ChunkParseOperator {

    var stringIndexes = mutableListOf<Int>()
    var styleIndexes = mutableListOf<Int>()

    override val chunkEndOffset: Int
        get() = TODO("Not yet implemented")

    override fun chunkParseOperator(): ChunkParseOperator = run {

        this
    }

    override fun toString(): String {
        return super.toString()
    }
}