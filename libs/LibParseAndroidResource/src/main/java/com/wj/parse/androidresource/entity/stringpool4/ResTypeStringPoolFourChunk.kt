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

    override val position: Int
        get() = 4

    override val childPosition: Int
        get() = 0

    /**
     * all childs of this chunk
     */
    override fun toString(): String =
        formatToString(
            chunkName = "Resource type String pool",
            "$resStringPoolHeader",
            "$resStringPoolRefOffset"
        )
}