package com.wj.parse.androidresource.entity.typespec6.type

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolRef
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk
import com.wj.parse.androidresource.entity.typespec6.type.complex.ResTableTypeMapEntityChildChildChunk
import com.wj.parse.androidresource.entity.typespec6.type.simple.ResTableTypeValueChildChildChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils

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
 *       FLAG_WEAK = 0x0004
 *   };
 *   uint16_t flags;
 *
 *   // Reference into ResTable_package::keyStrings identifying this entry.
 *   struct ResStringPool_ref key;
 * };
 * This is a entry in the type pool, it has a flag which is used to define this entity is complex or simple
 * if it is a complex resource, next parse [ResTableTypeMapEntityChildChildChunk], otherwise, [ResTableTypeValueChildChildChunk]
 */
open class ResTableTypeEntryChildChildChunk(
    /**
     * the string pool chunk byte array which index has started from 0 for this child chunk
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
    /**
     * all resource key list
     */
    private val resKeyStringList: MutableList<String>,
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

    var resKeyString: String = ""

    init {
        checkChunkAttributes()
        chunkParseOperator()
    }

    override val childPosition: Int
        get() = ChunkParseOperator.CHILD_CHILD_POSITION

    override val position: Int
        get() = ResTableTypeSpecAndTypeChunk.POSITION

    override val endOffset: Int
        get() = SIZE_IN_BYTE + FLAGS_IN_BYTE + KEY_IN_BYTE

    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header, because this is a child chunk without header.")
            null
        }

    override val chunkProperty
        get() = ChunkProperty.CHUNK_AREA_CHILD_CHILD

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
        if (key.index < resKeyStringList.size && key.index >= 0) {
            /**
             * TODO
             * If flags is 138, the index is out of this key strings list
             * If flags is [ResTableTypeValueChildChildChunk.DataType.TYPE_NULL] the index is -1
             */
            resKeyString = resKeyStringList[key.index]
        }
        return this
    }

    override fun toString(): String = formatToString(
        chunkName = "Res Table Entry(header of entry)",
        "size is $size, flags is ${Flags.valueOf(flags)}, key is $key, resourceKey is $resKeyString"
    )

    companion object {
        private const val SIZE_IN_BYTE = 2
        private const val FLAGS_IN_BYTE = 2
        private const val KEY_IN_BYTE = 4
    }

    enum class Flags(val value: Short) {

        NOT_SET_FLAG(0x0000),

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
        FLAG_WEAK(0x0004),

        /**
         * unknown flags
         */
        FLAG_UNKNOWN(-10000);

        companion object {
            fun valueOf(value: Short): String? = when (value) {
                NOT_SET_FLAG.value -> NOT_SET_FLAG.name
                FLAG_COMPLEX.value -> FLAG_COMPLEX.name
                FLAG_PUBLIC.value -> FLAG_PUBLIC.name
                FLAG_WEAK.value -> FLAG_WEAK.name
                else -> {
                    // TODO try to find another flags, for example, 56, 160, -1
                    Logger.debug("$value is a unknown value for this enum class")
                    FLAG_UNKNOWN.name
                }
            }
        }


    }
}
