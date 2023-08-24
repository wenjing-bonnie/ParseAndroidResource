package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolRef
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils
import java.lang.IllegalArgumentException

/**
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#1491
 * This is the beginning of information about an entry in the resource
 * table.  It holds the reference to the name of this entry, and is
 * immediately followed by one of:
 *   * A Res_value structure, if FLAG_COMPLEX is -not- set.
 *   * An array of ResTable_map structures, if FLAG_COMPLEX is set.
 *     These supply a set of name/value mappings of data.
 *
 *  struct ResTable_entry
 * {
 *   // Number of bytes in this structure.
 *   uint16_t size;
 *   enum {
 *       // If set, this is a complex entry, holding a set of name/value
 *       // mappings.  It is followed by an array of ResTable_map structures.
 *       FLAG_COMPLEX = 0x0001,
 *       // If set, this resource has been declared public, so libraries
 *       // are allowed to reference it.
 *       FLAG_PUBLIC = 0x0002,
 *       // If set, this is a weak resource and may be overriden by strong
 *       // resources of the same name/type. This is only useful during
 *       // linking with other resource tables.
 *       FLAG_WEAK = 0x0004,
 *   };
 *   uint16_t flags;
 *
 *   // Reference into ResTable_package::keyStrings identifying this entry.
 *   struct ResStringPool_ref key;
 * };
 */
class ResTableTypeEntryChunkChild(
    /**
     * the string pool chunk byte array which index has started from 0 for this child chunk
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
) : ChunkParseOperator {

    /**
     * Number of bytes in this structure.
     */
    var size: Short = 0
    var flags: Short = 0

    /**
     * Reference into ResTable_package::keyStrings identifying this entry.
     */
    lateinit var key: ResStringPoolRef

    init {
        // TODO move to parent and control by chunkProperty()???
        checkChunkAttributes()
        chunkParseOperator()
    }

    override val childPosition: Int
        get() = ChunkParseOperator.CHILD_CHILD_POSITION

    override val position: Int
        get() = ResTableTypeSpecAndTypeSixChunk.POSITION

    override val chunkEndOffset: Int
        get() = SIZE_IN_BYTE + FLAGS_IN_BYTE + KEY_IN_BYTE

    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header, because this is a child chunk without header.")
            null
        }

    override fun chunkProperty() = ChunkProperty.CHUNK_CHILD_CHILD

    override fun chunkParseOperator(): ChunkParseOperator {
        var attributeOffset = 0
        var attributeArrayByte = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            SIZE_IN_BYTE
        )
        size = Utils.byte2Short(attributeArrayByte)

        attributeOffset += SIZE_IN_BYTE
        attributeArrayByte = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            FLAGS_IN_BYTE
        )
        flags = Utils.byte2Short(attributeArrayByte)

        attributeOffset += FLAGS_IN_BYTE
        attributeArrayByte = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            KEY_IN_BYTE
        )
        key = ResStringPoolRef(Utils.byte2Int(attributeArrayByte))
        return this
    }

    override fun toString(): String = formatToString(
        chunkName = "Res Table Entry",
        "size is $size, flags is ${Flags.valueOf(flags)}, key is $key"
    )

    companion object {
        private const val SIZE_IN_BYTE = 2
        private const val FLAGS_IN_BYTE = 2
        private const val KEY_IN_BYTE = 4
    }

    enum class Flags(val value: Short) {
        /**
         * If set, this is a complex entry, holding a set of name/value
         * mappings.  It is followed by an array of ResTable_map structures.
         */
        FLAG_COMPLEX(0x0001),

        /**
         * If set, this resource has been declared public, so libraries
         * are allowed to reference it.
         */
        FLAG_PUBLIC(0x0002),

        /**
         * If set, this is a weak resource and may be overriden by strong
         * resources of the same name/type. This is only useful during
         * linking with other resource tables.
         */
        FLAG_WEAK(0x0004);

        companion object {
            fun valueOf(value: Short): String? = when (value) {
                FLAG_COMPLEX.value -> {
                    FLAG_COMPLEX.name
                }

                FLAG_PUBLIC.value -> FLAG_PUBLIC.name
                FLAG_WEAK.value -> FLAG_WEAK.name
                else -> {
                    throw IllegalArgumentException("A wrong value for this enum class")
                }
            }
        }


    }
}
