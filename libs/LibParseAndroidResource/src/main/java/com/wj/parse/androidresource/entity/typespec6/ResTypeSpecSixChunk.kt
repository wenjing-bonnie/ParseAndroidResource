package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.typespec6.ResTypeSpecAndTypeInfoSixChunk.Companion.ENTRY_COUNT_BYTE
import com.wj.parse.androidresource.entity.typespec6.ResTypeSpecAndTypeInfoSixChunk.Companion.ID_BYTE
import com.wj.parse.androidresource.entity.typespec6.ResTypeSpecAndTypeInfoSixChunk.Companion.RES0_BYTE
import com.wj.parse.androidresource.entity.typespec6.ResTypeSpecAndTypeInfoSixChunk.Companion.RES1_BYTE
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Utils
import kotlin.experimental.and

/**
 * create by wenjing.liu at 2023/7/29
 *
 * ResTable_typeSpec
 */
class ResTypeSpecSixChunk(
    override val inputResourceByteArray: ByteArray,
    override val startOffset: Int
) : ChunkParseOperator {
    var id: Int = -1
    var res0: Byte = -1
    var res1: Short = -1
    var entryCount: Int = -1

    override val header: ResChunkHeader
        get() = ResChunkHeader(resArrayStartZeroOffset)

    override val chunkEndOffset: Int
        get() = header.size

    override fun chunkParseOperator(): ChunkParseOperator {
        var attributeOffset = header.chunkEndOffset
        var attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, ID_BYTE)
        id = attributeByteArray?.let { idArray ->
            (idArray[0] and 0xFF.toByte()).toInt()
        } ?: -1

        // this res0 is standing by, it is 0
        attributeOffset += ID_BYTE
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, RES0_BYTE)
        res0 = attributeByteArray?.let { res0Array ->
            (res0Array[0] and 0xFF.toByte())
        }?: -1

        // this res1 is standing by, it is 0
        attributeOffset += RES0_BYTE
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, RES1_BYTE)
        res1 = Utils.byte2Short(attributeByteArray)

        attributeOffset += RES1_BYTE
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, ENTRY_COUNT_BYTE)
        entryCount = Utils.byte2Int(attributeByteArray)

        return this
    }

    override fun chunkProperty() = ChunkProperty.CHUNK


    override fun toString(): String =
        "Part6: ->Type spec header is ${header}\n" +
                "          id is $id, res0 is $res0, res1 is $res1,  entryCount is $entryCount \n" +
                "\nPart6: -> End..."

    companion object {
        const val SPEC_PUBLIC = 0x40000000
    }

}