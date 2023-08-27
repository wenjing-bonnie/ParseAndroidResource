package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolSecondChunk
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
    /**
     * Google Pool String. It comes from [ResStringPoolSecondChunk.resStringPoolRefOffset.globalStringList]
     */
    private val globalStringList: MutableList<String>,
) : ChunkParseOperator {

    var size: Short = 0
    var res0: Byte = 0
    var dataType: Byte = 0
    var data: Int = 0
    val dataString: String
        get() = when (dataType) {
            DataType.TYPE_STRING.value -> {
                if (data < globalStringList.size) {
                    globalStringList[data]
                } else {
                    throw IllegalArgumentException("this data $data is not illegal, can't find it in the global pool string list which size is only ${globalStringList.size}")
                }
            }

            DataType.TYPE_ATTRIBUTE.value -> {
                String.format("?${getPackage(data)}%08X", data)
            }

            DataType.TYPE_REFERENCE.value -> {
                String.format("@${getPackage(data)}%08X", data)
            }

            DataType.TYPE_FLOAT.value -> {
                // TODO
                java.lang.Float.intBitsToFloat(data).toString()
            }

            DataType.TYPE_INT_HEX.value -> {
                String.format("0x%08X", data)
            }

            DataType.TYPE_INT_BOOLEAN.value -> {
                if (data != 0) "true" else "false"
            }

            DataType.TYPE_DIMENSION.value -> {
                "${complexToFloat(data)}${DIMENSION_UNITS[data and COMPLEX_UNIT_MASK]}"
            }

            DataType.TYPE_FRACTION.value -> {
                "${complexToFloat(data)}${FRACTION_UNITS[data and COMPLEX_UNIT_MASK]}"
            }

            else -> {
                if (dataType >= DataType.TYPE_FIRST_COLOR_INT.value && dataType <= DataType.TYPE_LAST_COLOR_INT.value) {
                    String.format("#%08X", data)
                } else if (dataType >= DataType.TYPE_FIRST_INT.value && dataType <= DataType.TYPE_LAST_INT.value) {
                    data.toString()
                } else {
                    // invalid data type
                    String.format("<0x%X, type 0x%02X>", data, dataType)
                }
            }
        }

    init {
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
        var attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, SIZE_IN_BYTE)
        size = Utils.byte2Short(attributeByteArray)

        attributeOffset += SIZE_IN_BYTE
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, RES0_IN_BYTE)
        attributeByteArray?.let {
            res0 = it[0] and 0xFF.toByte()
        }

        attributeOffset += RES0_IN_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, DATA_TYPE_IN_BYTE)
        attributeByteArray?.let {
            dataType = it[0] and 0xFF.toByte()
        }

        attributeOffset += DATA_TYPE_IN_BYTE
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, DATA_IN_BYTE)
        data = Utils.byte2Int(attributeByteArray)
        return this
    }

    override val chunkProperty
        get() = ChunkProperty.CHUNK_CHILD_CHILD

    override fun toString() =
        formatToString(
            chunkName = "Res Table Value",
            "size is $size, res0 is $res0, dataType is ${DataType.valueOf(dataType)}, data is $data"
        )

    fun invalidDataType(dataString: String) =
        dataString.indexOf("<") >= 0

    companion object {
        private const val SIZE_IN_BYTE = 2
        private const val RES0_IN_BYTE = 1
        private const val DATA_TYPE_IN_BYTE = 1
        private const val DATA_IN_BYTE = 4
        private val RADIX_MULTS = floatArrayOf(
            0.00390625f, 3.051758E-005f, 1.192093E-007f, 4.656613E-010f
        )
        private val DIMENSION_UNITS = arrayOf(
            "px", "dip", "sp", "pt", "in", "mm", "", ""
        )

        private val FRACTION_UNITS = arrayOf(
            "%", "%p", "", "", "", "", "", ""
        )
        private const val COMPLEX_UNIT_PX = 0
        private const val COMPLEX_UNIT_DIP = 1
        private const val COMPLEX_UNIT_SP = 2
        private const val COMPLEX_UNIT_PT = 3
        private const val COMPLEX_UNIT_IN = 4
        private const val COMPLEX_UNIT_MM = 5
        private const val COMPLEX_UNIT_SHIFT = 0
        private const val COMPLEX_UNIT_MASK = 15
        private const val COMPLEX_UNIT_FRACTION = 0
        private const val COMPLEX_UNIT_FRACTION_PARENT = 1
        private const val COMPLEX_RADIX_23p0 = 0
        private const val COMPLEX_RADIX_16p7 = 1
        private const val COMPLEX_RADIX_8p15 = 2
        private const val COMPLEX_RADIX_0p23 = 3
        private const val COMPLEX_RADIX_SHIFT = 4
        private const val COMPLEX_RADIX_MASK = 3
        private const val COMPLEX_MANTISSA_SHIFT = 8
        private const val COMPLEX_MANTISSA_MASK = 0xFFFFFF
    }

    private fun getPackage(id: Int) =
        if (id ushr 24 == 1) {
            "android:"
        } else {
            ""
        }

    private fun complexToFloat(complex: Int): Float {
        return (complex and -0x100).toFloat() * RADIX_MULTS[complex shr 4 and 3]
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
                    throw IllegalArgumentException("$value is a wrong value for this enum class")
                }
            }
        }
    }
}
