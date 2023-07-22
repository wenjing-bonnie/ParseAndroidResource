package com.wj.parse.androidresource.interfaces

import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.utils.Logger
import java.lang.IllegalArgumentException

/**
 * Parse every chunk
 */
interface ChunkParseOperator {
    /**
     * start offset of this chunk
     */
    /**
     *  If this chunk is a whole chunk, [startOffset] should return the start offset behind last chunk
     *  if this chunk is a child chunk, [startOffset] should return 0, because the [resArrayStartZeroOffset] of child chunk has been changed to first byte array
     */
    val startOffset: Int

    /**
     *  If this chunk is a whole chunk, [chunkEndOffset] should return the end of this chunk in bit unit
     *  if this chunk is a child chunk, [chunkEndOffset] should return the size of this chunk in bit unit
     */
    val chunkEndOffset: Int

    /**
     * [resArrayStartZeroOffset] should start from end of previous chunk
     */
    val resArrayStartZeroOffset: ByteArray

    /**
     * parse chunk resource data
     */
    fun chunkParseOperator(): ChunkParseOperator

    fun chunkProperty(): ChunkProperty

    /**
     * check the attributes of this chunk have been set the collect value
     */
    fun checkChunkAttributes() =
        when (chunkProperty()) {
            ChunkProperty.CHUNK_CHILD -> {
                if (startOffset != 0) {
                    throw IllegalArgumentException("${this.javaClass.simpleName} is a child of chunk, the startOffset should be 0, because 'resArrayStartZeroOffset' has been changed to start from 0")
                }
                Logger.debug("${this.javaClass.simpleName} has set the collect values")
                true
            }

            ChunkProperty.CHUNK -> {

                if (this is ResourceTableHeaderFirstChunk && startOffset != 0) {
                    throw IllegalArgumentException("${this.javaClass.simpleName}  is a first chunk, the startOffset should be 0")
                }
                if (this !is ResourceTableHeaderFirstChunk && startOffset == 0) {
                    throw IllegalArgumentException("${this.javaClass.simpleName}  is a chunk, the startOffset should be not 0")
                }
                Logger.debug("${this.javaClass.simpleName} has set the collect values")
                true
            }
        }

}