package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils

/**
 * create by wenjing.liu at 2023/8/25
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#1531
 * /**
 * A single name/value mapping that is part of a complex resource
 * entry.
 * */
 * struct ResTable_map
 * {
 *   // The resource identifier defining this mapping's name.  For attribute
 *   // resources, 'name' can be one of the following special resource types
 *   // to supply meta-data about the attribute; for all other resource types
 *   // it must be an attribute resource.
 *   ResTable_ref name;
 *    // This mapping's value.
 *   Res_value value;
 * };
 */
class ResTableTypeMapChunkChild(
    /**
     * the string pool chunk byte array which index has started from 0 for this child chunk
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
) : ChunkParseOperator {
    lateinit var name: ResTableRef
    lateinit var value: ResTableTypeValueChunkChild

    init {
        checkChunkAttributes()
        chunkParseOperator()
    }

    override val position: Int
        get() = ResTableTypeSpecAndTypeSixChunk.POSITION

    override val childPosition: Int
        get() = ChunkParseOperator.CHILD_CHILD_POSITION

    override fun chunkProperty(): ChunkProperty = ChunkProperty.CHUNK_CHILD_CHILD

    override val chunkEndOffset: Int
        get() = TODO("Not yet implemented")

    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header, because this is a child chunk without header.")
            null
        }

    override fun chunkParseOperator(): ChunkParseOperator {
        var attributeOffset = 0
        var attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, ResTableRef.SIZE_IN_BYTE)
        name = ResTableRef(Utils.byte2Int(attributeByteArray))

        attributeOffset += ResTableRef.SIZE_IN_BYTE
        value = ResTableTypeValueChunkChild(resArrayStartZeroOffset, attributeOffset)
        return this
    }

    override fun toString() = formatToString(
        chunkName = "Res Table Entry Map",
        "name is $name, value is $value"
    )

    /**
     * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#417
     *  This is a reference to a unique entry (a ResTable_entry structure)
     *  in a resource table.  The value is structured as: 0xpptteeee,
     *  where pp is the package index, tt is the type index in that
     *  package, and eeee is the entry index in that type.  The package
     *  and type values start at 1 for the first item, to help catch cases
     *  where they have not been supplied.
     * struct ResTable_ref
     * {
     *    uint32_t ident;
     * };
     */
    data class ResTableRef(val ident: Int) {
        companion object {
            const val SIZE_IN_BYTE = 4
        }
    }
}
