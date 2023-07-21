package com.wj.parse.androidresource.parse

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.ResStringPoolHeaderSecondChunk
import com.wj.parse.androidresource.entity.ResStringPoolRefSecondChunk
import com.wj.parse.androidresource.entity.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.utils.Logger

class ParseResourceManager {

    /**
     * struct ResTable_header
     * @param sourceByteArray
     */
    fun parseResourceTableHeaderFirstChunk(
        sourceByteArray: ByteArray
    ): ResourceTableHeaderFirstChunk =
        ResourceTableHeaderFirstChunk(sourceByteArray).apply {
            Logger.debug(toString())
        }

    /**
     * struct ResStringPool_header, this chunk is behind [ResourceTableHeaderFirstChunk]
     * @param sourceByteArray
     * @param startOffset  startOffset = 0, [sourceByteArray] should be whole Array; startOffset > 0, [sourceByteArray] should start from startOffset
     */
    fun parseResStringPoolHeaderSecondChunk(
        sourceByteArray: ByteArray,
        startOffset: Int = 0
    ): ResStringPoolHeaderSecondChunk =
        ResStringPoolHeaderSecondChunk(sourceByteArray, startOffset).apply {
            Logger.debug(toString())
        }

    fun parseResStringPoolRefSecondChunk(
        sourceByteArray: ByteArray,
        startOffset: Int = 0
    ): ResStringPoolRefSecondChunk =
        ResStringPoolRefSecondChunk(sourceByteArray, startOffset).apply {
            Logger.debug(toString())
        }
}