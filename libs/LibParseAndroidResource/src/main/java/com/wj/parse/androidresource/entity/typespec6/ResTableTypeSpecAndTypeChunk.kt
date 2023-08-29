package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.package3.ResTablePackageThirdChunk
import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolChunk
import com.wj.parse.androidresource.entity.stringpool4.ResTypeStringPoolChunk
import com.wj.parse.androidresource.entity.stringpool5.ResKeyStringsPoolChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.interfaces.ChunkType
import com.wj.parse.androidresource.parse.ResourceElementsManager
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils

/**
 * create by wenjing.liu at 2023/7/29
 */
class ResTableTypeSpecAndTypeChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderChunk.chunkEndOffset] + [ResGlobalStringPoolChunk.chunkEndOffset] + [ResTablePackageThirdChunk.keyStrings] + [ResKeyStringsPoolChunk.chunkEndOffset]
     */
    override val startOffset: Int,
    /**
     * Google Pool String. It comes from [ResGlobalStringPoolChunk.resStringPoolRefOffset.globalStringList]
     */
    val globalStringList: MutableList<String>,
    /**
     * all resource type list: [attr, drawable, layout, anim, raw, color, dimen, string, style, id]
     * It comes from [ResTypeStringPoolChunk.resStringPoolRefOffset.globalStringList]
     */
    val typeStringList: MutableList<String>,
    /**
     * all resource key list
     * It comes from [ResKeyStringsPoolChunk.resStringPoolRefOffset.globalStringList]
     */
    val keyStringList: MutableList<String>,
    /**
     * [ResTablePackageThirdChunk.id]
     */
    private val packageId: Int,
) : ChunkParseOperator {
    // TODO consider where is it ?
    var resourceElementsManager = ResourceElementsManager()
    private val typeChunks = mutableListOf<ChunkParseOperator>()
    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header because this chunk has many childs")
            null
        }

    init {
        checkChunkAttributes()
        chunkParseOperator()
    }

    /**
     * TODO this is wrong, should consider
     */
    override val chunkEndOffset: Int
        get() = TODO()

    override val position: Int
        get() = POSITION

    override val childPosition: Int
        get() = 0

    override fun chunkParseOperator(): ChunkParseOperator {
        var endOffset = startOffset
        val sourceSize = inputResourceByteArray.size
        var childByteArray: ByteArray? = resArrayStartZeroOffset
        // loop every chunk
        var chunkPosition = 1
        var resTypeSpecId = 0
        while (!isTypeChunkParsingCompleted(endOffset = endOffset, sourceSize = sourceSize)) {
            childByteArray?.let { child ->
                val childHeader = ResChunkHeader(child)
                // the offset is ok.
                // Logger.debug("sourceSize is $sourceSize, endOffset is $endOffset, header.type is ${childHeader.type}")
                when (childHeader.type) {
                    ChunkType.RES_TABLE_TYPE_SPEC_TYPE.value -> {
                        val typeChunk = ResTableTypeSpecChildChunk(
                            inputResourceByteArray,
                            endOffset,
                            chunkPosition
                        )
                        typeChunks.add(typeChunk)
                        // go to next chunk
                        endOffset += typeChunk.chunkEndOffset

                        resTypeSpecId = typeChunk.id
                    }

                    else -> {
                        val typeChunk =
                            ResTableTypeChildChunk(
                                inputResourceByteArray,
                                endOffset,
                                chunkPosition,
                                globalStringList = globalStringList,
                                resTypeStringList = typeStringList,
                                resKeyStringList = keyStringList,
                                packageId = packageId,
                                resTypeSpecId = resTypeSpecId,
                                resourceElementsManager = resourceElementsManager
                            )

                        typeChunks.add(typeChunk)
                        // go to next chunk
                        endOffset += typeChunk.chunkEndOffset
                    }
                }
            }
            // go to next chunk
            chunkPosition += 1
            childByteArray =
                Utils.copyByte(inputResourceByteArray, endOffset) ?: run {
                    Logger.debug(
                        "\n ^^^^^^^^ \n" +
                                " !Oh, thanks goodness, we has finished to parse all resource type symbol table and resource key symbol table in the ${this.javaClass.simpleName}" +
                                "\n ^^^^^^^^ \n"
                    )
                    null
                }
        }
        return this
    }

    private fun isTypeChunkParsingCompleted(endOffset: Int, sourceSize: Int): Boolean =
        endOffset >= sourceSize

    /**
     * this chunk area is special, it is part of table package.
     */
    override val chunkProperty
        get() = ChunkProperty.CHUNK_AREA

    override fun toString(): String =
        formatToString(
            chunkName = "Res Table Type Spec and Type",
            *typeChunks.map {
                it.toString()
            }.toTypedArray(),
            resourceElementsManager.toString()
        )

    companion object {
        const val SPEC_PUBLIC = 0x40000000
        const val SPEC_STAGED_API = 0x20000000u
        const val POSITION = 6
        const val ID_BYTE = 1
        const val RES0_BYTE = 1
        const val RES1_BYTE = 2
        const val ENTRY_COUNT_BYTE = 4
        const val ENTRIES_START_BYTE = 4
    }

}
