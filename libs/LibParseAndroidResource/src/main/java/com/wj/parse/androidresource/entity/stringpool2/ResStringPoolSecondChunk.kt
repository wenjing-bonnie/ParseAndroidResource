package com.wj.parse.androidresource.entity.stringpool2

import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger

/**
 * This is the second chunk of resource.arsc file.
 */
class ResStringPoolSecondChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderFirstChunk.chunkEndOffset]
     */
    override val startOffset: Int
) : ChunkParseOperator {

    lateinit var resStringPoolHeader: ResStringPoolHeaderChunkChild
    lateinit var resStringPoolRefOffset: ResStringPoolContentChunkChild

    /**
     * The end offset byte of this chunk
     */
    override val chunkEndOffset: Int
        get() = run {
            resStringPoolHeader.header.size
        }.takeIf {
            ::resStringPoolHeader.isInitialized
        }?.let { size ->
            size
        }
            ?: (startOffset + resStringPoolHeader.chunkEndOffset + resStringPoolRefOffset.chunkEndOffset)

//            run {
//            // maybe 2 byte 有其他作用
//            val end =
//                startOffset + resStringPoolHeader.chunkEndOffset + resStringPoolRefOffset.chunkEndOffset
//            Logger.debug("startOffset+header is ${startOffset + resStringPoolHeader.chunkEndOffset}")
//            Logger.debug("resStringPoolRefOffset is ${ resStringPoolRefOffset.chunkEndOffset}")
//            Logger.debug("childOffset is ${ resStringPoolRefOffset.childOffset}")
//
//            Logger.debug("resStringPoolHeader.header.size-resStringPoolHeader.header.headerSize is ${resStringPoolHeader.header.size - resStringPoolHeader.header.headerSize}, resStringPoolRefOffset.chunkEndOffset is ${resStringPoolRefOffset.chunkEndOffset} ")
//            run {
//                Logger.debug("compute end size is $end, and the size in the header is ${resStringPoolHeader.header.size}")
//                resStringPoolHeader.header.size
//            }.takeIf {
//                ::resStringPoolHeader.isInitialized
//            }
//                end
//        }

    override fun chunkProperty(): ChunkProperty = ChunkProperty.CHUNK

    init {
        chunkParseOperator()
        checkChunkAttributes()
    }

    override fun chunkParseOperator(): ChunkParseOperator {
        // header
        resStringPoolHeader =
            ResStringPoolHeaderChunkChild(resArrayStartZeroOffset)
        var childStartOffsetInParent = resStringPoolHeader.chunkEndOffset
        // string offset and style offset
        resStringPoolRefOffset = ResStringPoolContentChunkChild(
            resArrayStartZeroOffset,
            startOffset = childStartOffsetInParent,
            stringCount = resStringPoolHeader.stringCount,
            styleCount = resStringPoolHeader.styleCount,
            stringStart = resStringPoolHeader.stringStart,
            stylesStart = resStringPoolHeader.stylesStart
        )
        return this
    }

    /**
     * all childs of this chunk
     */
    override fun toString(): String =
        "Part2: -> First child =>=> ${resStringPoolHeader}\n" +
                "          Second child =>=> $resStringPoolRefOffset" +
                "\nPart2: -> End..."

}