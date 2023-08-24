package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import java.lang.IllegalArgumentException

/**
 * create by wenjing.liu at 2023/8/25
 *
 * struct Res_value
 * {
 *      //Res_value
 *      uint16_t size;
 *      //
 *      uint8_t res0;
 */
class ResTableTypeValueChunkChild(
    /**
     * the string pool chunk byte array which index has started from 0 for this child chunk
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
) : ChunkParseOperator {

    var size: Short = 0
    var res0: Byte = 0
    var dataType: Byte = 0
    var data: Int = 0

    init {
        // TODO move to parent and control by chunkProperty()???
        checkChunkAttributes()
        chunkParseOperator()
    }

    override val childPosition: Int
        get() = ChunkParseOperator.CHILD_CHILD_POSITION

    override val position: Int
        get() = ResTableTypeSpecAndTypeSixChunk.POSITION

    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header, because this is a child chunk without header.")
            null
        }
    override val chunkEndOffset: Int
        get() = SIZE_IN_BYTE + RES0_IN_BYTE + DATA_TYPE_IN_BYTE + DATA_IN_BYTE

    override fun chunkParseOperator(): ChunkParseOperator {
        TODO("Not yet implemented")
    }

    override fun chunkProperty() = ChunkProperty.CHUNK_CHILD_CHILD

    override fun toString() =
        formatToString(
            chunkName = "Res Table Value",
            "size is $size, res0 is $res0, dataType is $dataType, data is $data"
        )

    companion object {
        private const val SIZE_IN_BYTE = 2
        private const val RES0_IN_BYTE = 1
        private const val DATA_TYPE_IN_BYTE = 1
        private const val DATA_IN_BYTE = 4
    }

    enum class DataType(val value: Byte) {
        TYPE_NULL(0x0),
        TYPE_REFERENCE(0x01),
        TYPE_ATTRIBUTE(0x02),
        TYPE_STRING(0x03),
        TYPE_FLOAT(0x04),
        TYPE_DIMENSION(0x05),
        TYPE_FRACTION(0x06),
        TYPE_FIRST_INT(0x10),
        TYPE_INT_DEC(0x10),
        TYPE_INT_HEX(0x11),
        TYPE_INT_BOOLEAN(0x12),
        TYPE_FIRST_COLOR_INT(0x1c),
        TYPE_INT_COLOR_ARGB8(0x1c),
        TYPE_INT_COLOR_RGB8(0x1d),
        TYPE_INT_COLOR_ARGB4(0x1e),
        TYPE_INT_COLOR_RGB4(0x1f),
        TYPE_LAST_COLOR_INT(0x1f),
        TYPE_LAST_INT(0x1f);

        fun valueOf(value: Byte) = when (value) {
            TYPE_NULL.value -> TYPE_NULL.name
            TYPE_REFERENCE.value -> TYPE_REFERENCE.name
            TYPE_ATTRIBUTE.value -> TYPE_ATTRIBUTE.name
            TYPE_STRING.value -> TYPE_STRING.name
            TYPE_FLOAT.value -> TYPE_FLOAT.name
            TYPE_DIMENSION.value -> TYPE_DIMENSION.name
            TYPE_FRACTION.value -> TYPE_FRACTION.name
            TYPE_FIRST_INT.value -> TYPE_FIRST_INT.name
            TYPE_INT_DEC.value -> TYPE_FIRST_INT.name
            TYPE_INT_HEX.value -> TYPE_FIRST_INT.name
            TYPE_INT_BOOLEAN.value -> TYPE_INT_BOOLEAN.name
            TYPE_FIRST_COLOR_INT.value -> TYPE_FIRST_COLOR_INT.name
            TYPE_INT_COLOR_ARGB8.value -> TYPE_INT_COLOR_ARGB8.name
            TYPE_INT_COLOR_RGB8.value -> TYPE_INT_COLOR_RGB8.name
            TYPE_INT_COLOR_ARGB4.value -> TYPE_INT_COLOR_ARGB4.name
            TYPE_INT_COLOR_RGB4.value -> TYPE_INT_COLOR_RGB4.name
            TYPE_LAST_COLOR_INT.value -> TYPE_LAST_COLOR_INT.name
            TYPE_LAST_INT.value -> TYPE_LAST_INT.name
            else -> {
                throw IllegalArgumentException("A wrong value for this enum class")
            }
        }
    }
}