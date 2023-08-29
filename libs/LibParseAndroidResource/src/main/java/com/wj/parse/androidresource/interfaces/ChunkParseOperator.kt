package com.wj.parse.androidresource.interfaces

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils
import java.lang.IllegalStateException

/**
 * Parse every chunk
 */
interface ChunkParseOperator {
    /**
     * every chunk has a header
     */
    val header: ResChunkHeader?
    /**
     * start offset of this chunk
     */
    /**
     *  If this chunk is a whole chunk, [startOffset] should return the start offset behind last chunk
     *  if this chunk is a child chunk, [startOffset] should return 0, because the [resArrayStartZeroOffset] of child chunk has been changed index to first byte array
     */
    val startOffset: Int

    /**
     * Normally this chunkEndOffset is [ChunkParseOperator.header.size]
     *  If this chunk is a whole chunk, [chunkEndOffset] should return the end of this chunk in bit unit
     *  if this chunk is a child chunk, [chunkEndOffset] should return the size of this chunk in bit unit
     */
    val chunkEndOffset: Int

    /**
     * the whole resource byte array of this resource.arsc file
     */
    val inputResourceByteArray: ByteArray

    /**
     * The position of this chunk in the .arsc
     */
    val position: Int

    /**
     * If this chunk is a child chunk, it is the position in this part
     */
    val childPosition: Int

    /**
     * [resArrayStartZeroOffset] should be the byte array which index start from 0 without the previous chunk byte data.
     * Therefore we can read first attribute data of this chunk which start from [ResChunkHeader.chunkEndOffset]
     */
    val resArrayStartZeroOffset: ByteArray
        get() = kotlin.run {
            // Logger.debug("${inputResourceByteArray.size} startOffset is $startOffset")
            Utils.copyByte(inputResourceByteArray, startOffset) ?: kotlin.run {
                Logger.error("${this.javaClass.simpleName} has a bad state, the array is null, from $inputResourceByteArray starting from $startOffset")
                throw IllegalStateException("${this.javaClass.simpleName} has a bad state, the array is null")
            }
        }

    fun startParseChunk(): ChunkParseOperator =
        when (chunkProperty) {
            ChunkProperty.CHUNK_AREA_REUSED,
            -> {
                checkChunkAttributes()
                chunkParseOperator()
                this
            }

            else -> {
                throw IllegalCallerException("${this.javaClass.simpleName} will start parse chunk automatically, don't need to call this method")
            }
        }

    /**
     * parse chunk resource data
     */
    fun chunkParseOperator(): ChunkParseOperator

    val chunkProperty: ChunkProperty

    /**
     * check the attributes of this chunk have been set the collect value
     * TODO
     */
    fun checkChunkAttributes() =
        when (chunkProperty) {
            ChunkProperty.CHUNK_AREA_HEADER -> {
//                if (startOffset != 0) {
//                    throw IllegalArgumentException("${this.javaClass.simpleName} is a header chunk, the startOffset should be 0, because 'resArrayStartZeroOffset' has been changed index to start from 0")
//                }
                // Logger.debug("** Check ${this.javaClass.simpleName} attributes is great! ** it has set the collect values, start the parse flow .... ")
                true
            }

            ChunkProperty.CHUNK_AREA_HEADER -> {
                // the first chunk and the startOffset should be 0
//                if (this is ResourceTableHeaderChunk && startOffset != 0) {
//                    throw IllegalArgumentException("${this.javaClass.simpleName}  is a first chunk, the startOffset should be 0")
//                }
//                // not first chunk and the startOffset shouldn't be 0
//                if ((this !is ResourceTableHeaderChunk) && startOffset == 0) {
//                    throw IllegalArgumentException("${this.javaClass.simpleName} is a chunk, the startOffset should be not 0")
//                }
                // Logger.debug("** Check attributes is great! **  ${this.javaClass.simpleName} has set the collect values, start the parse flow ....")
                true
            }

            else -> {
                //TODO have no idea about checking
            }
        }

    /**
     *  format toString()
     */
    fun formatToString(
        chunkName: String,
        vararg chunkInfo: String
    ): String {
        var start = "Part$position: $chunkName:"
        var end = "\n${Logger.TITLE_TAG_START} Part $position is ended ${Logger.TITLE_TAG_END}"
        when (chunkProperty) {
            ChunkProperty.CHUNK_AREA_CHILD,
            ChunkProperty.CHUNK_AREA_CHILD_CHILD-> {
                start = ">> No.$childPosition child $chunkName in a chunk area"
                end =
                    "\n${Logger.TAG_SPACE}${Logger.END_TAG_START} No. $childPosition child chunk is ended ${Logger.END_TAG_END}"
            }
            else -> {

            }
        }
        return "$start${
            chunkInfo.joinToString {
                "\n${Logger.TAG_SPACE}$it"
            }
        }$end"
    }

    companion object {
        const val CHILD_CHILD_POSITION = 10000
    }

}