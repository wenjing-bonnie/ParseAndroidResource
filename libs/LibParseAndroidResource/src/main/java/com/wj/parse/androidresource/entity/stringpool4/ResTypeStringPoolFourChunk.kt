package com.wj.parse.androidresource.entity.stringpool4

import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolSecondChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk

/**
 * create by wenjing.liu at 2023/7/27
 */
class ResTypeStringPoolFourChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderFirstChunk.chunkEndOffset]
     */
    override val startOffset: Int
) : ResStringPoolSecondChunk(inputResourceByteArray, startOffset) {

    /**
     * all childs of this chunk
     */
    override fun toString(): String =
        "Part4: -> First child =>=> ${resStringPoolHeader}\n" +
                "          Second child =>=> $resStringPoolRefOffset" +
                "\nPart4: -> End..."
}