package com.wj.parse.androidresource.interfaces

/**
 * create by wenjing.liu at 2023/7/22
 */
interface ChunkParseEvent : ChunkParseOperator {
    /**
     *
     */
    // var nextChunk: ChunkParseOperator
    /**
     * the resource bte array should start from end of previous chunk
     */
   // val resourceByteArray: ByteArray
}