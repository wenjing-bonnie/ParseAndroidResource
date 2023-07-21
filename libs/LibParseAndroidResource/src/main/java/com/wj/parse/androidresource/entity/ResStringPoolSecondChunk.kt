package com.wj.parse.androidresource.entity

import com.wj.parse.androidresource.interfaces.ChunkParseOperator


class ResStringPoolSecondChunk(
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

    lateinit var resStringPoolHeaderSecondChunk: ResStringPoolHeaderSecondChunk
    lateinit var resStringPoolRefSecondChunk: ResStringPoolRefSecondChunk

    init {
        chunkParseOperator()
    }

    override val chunkEndOffset: Int
        get() = TODO("Not yet implemented")

    override fun chunkParseOperator(): ChunkParseOperator = run {
        resStringPoolHeaderSecondChunk =
            ResStringPoolHeaderSecondChunk(resourceByteArray, startOffset)
        // resStringPoolRefSecondChunk =
        TODO("Not yet implemented")
        this
    }
}