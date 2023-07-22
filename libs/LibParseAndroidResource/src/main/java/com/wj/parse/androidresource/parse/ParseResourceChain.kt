package com.wj.parse.androidresource.parse

import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolSecondChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.utils.Logger

/**
 * the whole resource byte array
 */
class ParseResourceChain(resourceByteArray: ByteArray) {

    init {
        /** read [ResourceTableHeaderFirstChunk]*/
        Logger.debug("....... begin to read first chunk: Table Header ......")
        val tableHeaderChunk = ResourceTableHeaderFirstChunk(resourceByteArray).apply {
            Logger.debug(toString())
        }
        /** read [ResStringPoolSecondChunk] */
        Logger.debug("...... begin to read second chunk: String Pool ......")
        val stringPoolHeader =
            ResStringPoolSecondChunk(resourceByteArray, tableHeaderChunk.chunkEndOffset).apply {
                Logger.debug(toString())
            }
    }
}