package com.wj.parse.androidresource.entity.stringpool5

import com.wj.parse.androidresource.entity.package3.ResTablePackageHeaderThirdChunk
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolSecondChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.utils.Logger

/**
 * create by wenjing.liu at 2023/7/27
 */
class ResKeyStringsPoolFiveChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderFirstChunk.chunkEndOffset] + [ResStringPoolSecondChunk.chunkEndOffset]  + [ResTablePackageHeaderThirdChunk.keyStrings]
     *                                    +[]
     */
    override val startOffset: Int
) : ResStringPoolSecondChunk(inputResourceByteArray, startOffset) {

    override val position: Int
        get() = 5

    override val childPosition: Int
        get() = 0

    /**
     * all childs of this chunk
     * TODO consider how to toString()
     */
    override fun toString(): String =
        formatToString(
            chunkName = "Resource Key String Pool",
            "$resStringPoolHeader",
            "$resStringPoolRefOffset"
        )
}
