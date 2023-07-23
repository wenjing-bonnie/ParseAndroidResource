package com.wj.parse.androidresource.interfaces

import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * Parse every chunk
 */
interface ChunkParseOperator {
    /**
     * start offset of this chunk
     */
    /**
     *  If this chunk is a whole chunk, [startOffset] should return the start offset behind last chunk
     *  if this chunk is a child chunk, [startOffset] should return 0, because the [resArrayStartZeroOffset] of child chunk has been changed index to first byte array
     */
    val startOffset: Int

    /**
     *  If this chunk is a whole chunk, [chunkEndOffset] should return the end of this chunk in bit unit
     *  if this chunk is a child chunk, [chunkEndOffset] should return the size of this chunk in bit unit
     */
    val chunkEndOffset: Int

    /**
     * the whole resource byte array of this resource.arsc file
     */
    val inputResourceByteArray: ByteArray

    /**
     * [resArrayStartZeroOffset] should be the byte array which index start from 0
     */
    val resArrayStartZeroOffset: ByteArray
        get() = Utils.copyByte(inputResourceByteArray, startOffset) ?: kotlin.run {
            Logger.error("${this.javaClass.simpleName}has a bad state, the array is null")
            throw IllegalStateException("${this.javaClass.simpleName} has a bad state, the array is null")
        }


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
            ChunkProperty.CHUNK_FIRST_CHILD -> {
                if (startOffset != 0) {
                    throw IllegalArgumentException("${this.javaClass.simpleName} is a child of chunk, the startOffset should be 0, because 'resArrayStartZeroOffset' has been changed index to start from 0")
                }
                Logger.debug("** Great! **  ${this.javaClass.simpleName} has set the collect values, start the parse flow .... ")
                true
            }

            else -> {

                if (this is ResourceTableHeaderFirstChunk && startOffset != 0) {
                    throw IllegalArgumentException("${this.javaClass.simpleName}  is a first chunk, the startOffset should be 0")
                }
                if (this !is ResourceTableHeaderFirstChunk && startOffset == 0) {
                    throw IllegalArgumentException("${this.javaClass.simpleName} is a chunk, the startOffset should be not 0")
                }
                Logger.debug("** Great! **  ${this.javaClass.simpleName} has set the collect values, start the parse flow ....")
                true
            }
        }
}