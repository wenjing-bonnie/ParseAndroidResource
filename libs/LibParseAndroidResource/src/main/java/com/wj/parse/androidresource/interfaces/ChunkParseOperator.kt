package com.wj.parse.androidresource.interfaces

/**
 * Parse every chunk
 */
interface ChunkParseOperator {
    /**
     *  The end of this chunk bit size
     */
    val chunkEndOffset: Int

    /**
     * parse chunk resource data
     */
    fun chunkParseOperator():ChunkParseOperator
}