package com.wj.parse.androidresource.entity.typespec6.type.complex

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolChunk
import com.wj.parse.androidresource.entity.typespec6.type.complex.ResTableTypeMapEntityChildChildChunk.ResTableRef
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk
import com.wj.parse.androidresource.entity.typespec6.type.simple.ResTableTypeValueChildChildChunk
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
class ResTableTypeMapChildChildChunk(
    /**
     * the string pool chunk byte array which index has started from 0 for this child chunk
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
    /**
     * Google Pool String. It comes from [ResGlobalStringPoolChunk.resStringPoolRefOffset.globalStringList]
     */
    private val globalStringList: MutableList<String>,
) : ChunkParseOperator {
    lateinit var name: ResTableRef
    lateinit var value: ResTableTypeValueChildChildChunk

    init {
        checkChunkAttributes()
        chunkParseOperator()
    }

    override val position: Int
        get() = ResTableTypeSpecAndTypeChunk.POSITION

    override val childPosition: Int
        get() = ChunkParseOperator.CHILD_CHILD_POSITION

    override val chunkProperty
        get() = ChunkProperty.CHUNK_AREA_CHILD_CHILD

    /**
     * It is 12 byte
     */
    override val endOffset: Int
        get() = ResTableRef.SIZE_IN_BYTE + value.endOffset

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
        value =
            ResTableTypeValueChildChildChunk(
                resArrayStartZeroOffset,
                attributeOffset,
                globalStringList
            )
        return this
    }

    override fun toString() = formatToString(
        chunkName = "${Logger.FOURTH_LEVEL} Res Table Entry Map(attribute) ${Logger.FOURTH_LEVEL}",
        "an attribute $name",
        "value is $value"
    )
}
