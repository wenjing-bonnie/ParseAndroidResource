package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils
import kotlin.experimental.and

/**
 * create by wenjing.liu at 2023/8/25
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#282
 * /**
 *  Representation of a value in a resource, supplying type
 *  information.
 * */
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
        var attributeOffset = 0
        var attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, SIZE_IN_BYTE)
        size = Utils.byte2Short(attributeByteArray)

        attributeOffset += SIZE_IN_BYTE
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, RES0_IN_BYTE)
        attributeByteArray?.let {
            res0 = it[0] and 0xFF.toByte()
        }

        attributeOffset += RES0_IN_BYTE
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, DATA_TYPE_IN_BYTE)
        attributeByteArray?.let {
            dataType = it[0] and 0xFF.toByte()
        }

        attributeOffset += DATA_TYPE_IN_BYTE
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, DATA_IN_BYTE)
        data = Utils.byte2Int(attributeByteArray)
        return this
    }

    override fun chunkProperty() = ChunkProperty.CHUNK_CHILD_CHILD

    override fun toString() =
        formatToString(
            chunkName = "Res Table Value",
            "size is $size, res0 is $res0, dataType is ${DataType.valueOf(dataType)}, data is $data"
        )

    companion object {
        private const val SIZE_IN_BYTE = 2
        private const val RES0_IN_BYTE = 1
        private const val DATA_TYPE_IN_BYTE = 1
        private const val DATA_IN_BYTE = 4
    }

    enum class DataType(val value: Byte) {
        /**
         * The 'data' is either 0 or 1, specifying this resource is either
         * undefined or empty, respectively.
         */
        TYPE_NULL(0x0),

        /**
         * The 'data' holds a ResTable_ref, a reference to another resource
         * table entry.
         */
        TYPE_REFERENCE(0x01),

        /**
         * The 'data' holds an attribute resource identifier.
         */
        TYPE_ATTRIBUTE(0x02),

        /**
         * The 'data' holds an index into the containing resource table's
         * global value string pool.
         */
        TYPE_STRING(0x03),

        /**
         * The 'data' holds a single-precision floating point number.
         */
        TYPE_FLOAT(0x04),

        /**
         * The 'data' holds a complex number encoding a dimension value,
         * such as "100in".
         */
        TYPE_DIMENSION(0x05),

        /**
         * The 'data' holds a complex number encoding a fraction of a
         * container.
         */
        TYPE_FRACTION(0x06),

        /**
         * The 'data' holds a dynamic ResTable_ref, which needs to be
         * resolved before it can be used like a TYPE_REFERENCE.
         */
        TYPE_DYNAMIC_REFERENCE(0x07),

        /**
         * The 'data' holds an attribute resource identifier, which needs to be resolved
         * before it can be used like a TYPE_ATTRIBUTE.
         */
        TYPE_DYNAMIC_ATTRIBUTE(0x08),

        /**
         * Beginning of integer flavors...
         * TODO maybe there will be a bug for Google
         */
        TYPE_FIRST_INT(0x10),

        /**
         * The 'data' is a raw integer value of the form n..n.
         */
        TYPE_INT_DEC(0x10),

        /**
         * The 'data' is a raw integer value of the form 0xn..n.
         */
        TYPE_INT_HEX(0x11),

        /**
         * The 'data' is either 0 or 1, for input "false" or "true" respectively.
         */
        TYPE_INT_BOOLEAN(0x12),

        /**
         * Beginning of color integer flavors...
         */
        TYPE_FIRST_COLOR_INT(0x1c),

        /**
         * The 'data' is a raw integer value of the form #aarrggbb.
         */
        TYPE_INT_COLOR_ARGB8(0x1c),

        /**
         * The 'data' is a raw integer value of the form #rrggbb.
         */
        TYPE_INT_COLOR_RGB8(0x1d),

        /**
         * The 'data' is a raw integer value of the form #argb.
         */
        TYPE_INT_COLOR_ARGB4(0x1e),

        /**
         * The 'data' is a raw integer value of the form #rgb.
         */
        TYPE_INT_COLOR_RGB4(0x1f),

        /**
         * ...end of integer flavors.
         */
        TYPE_LAST_COLOR_INT(0x1f),

        /**
         * ...end of integer flavors.
         */
        TYPE_LAST_INT(0x1f);

        companion object {
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
}
