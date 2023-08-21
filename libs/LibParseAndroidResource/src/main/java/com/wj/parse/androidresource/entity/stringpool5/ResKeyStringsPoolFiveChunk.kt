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

    /**
     * all childs of this chunk
     */
    override fun toString(): String =
        "Part5: -> First child =>=> ${resStringPoolHeader}\n" +
                "${Logger.TAG_SPACE}Second child =>=> $resStringPoolRefOffset\n" +
                "${Logger.TAG_SPACE}>>> Part5 is Ended <<<"
}