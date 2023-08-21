package com.wj.parse.androidresource.entity.stringpool4

import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolSecondChunk
import com.wj.parse.androidresource.entity.package3.ResTablePackageHeaderThirdChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.utils.Logger

/**
 * create by wenjing.liu at 2023/7/27
 */
class ResTypeStringPoolFourChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderFirstChunk.chunkEndOffset] + [ResStringPoolSecondChunk.chunkEndOffset] + [ResTablePackageHeaderThirdChunk.typeStrings]
     */
    override val startOffset: Int
) : ResStringPoolSecondChunk(inputResourceByteArray, startOffset) {

    /**
     * all childs of this chunk
     */
    override fun toString(): String =
        formatToString(
            part = 4,
            childPart = 0,
            chunkName = "Resource type String pool",
            "First child",
            "$resStringPoolHeader",
            "Second child:",
            "$resStringPoolRefOffset"
        )
}