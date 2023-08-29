package com.wj.parse.androidresource.entity.stringpool4

import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolSecondChunk
import com.wj.parse.androidresource.entity.package3.ResTablePackageThirdChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderChunk

/**
 * create by wenjing.liu at 2023/7/27
 */
class ResTypeStringPoolFourChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderChunk.chunkEndOffset] + [ResGlobalStringPoolSecondChunk.chunkEndOffset] + [ResTablePackageThirdChunk.typeStrings]
     */
    override val startOffset: Int
) : ResGlobalStringPoolSecondChunk(inputResourceByteArray, startOffset) {

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