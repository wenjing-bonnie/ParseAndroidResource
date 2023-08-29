package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolSecondChunk
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeMapEntityChunkChild.ResTableRef
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
    /**
     * Google Pool String. It comes from [ResGlobalStringPoolSecondChunk.resStringPoolRefOffset.globalStringList]
     */
    private val globalStringList: MutableList<String>,
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

    override val chunkProperty
        get() = ChunkProperty.CHUNK_AREA_CHILD_CHILD

    override val chunkEndOffset: Int
        get() = ResTableRef.SIZE_IN_BYTE + value.chunkEndOffset

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
            ResTableTypeValueChunkChild(resArrayStartZeroOffset, attributeOffset, globalStringList)
        return this
    }

    override fun toString() = formatToString(
        chunkName = "Res Table Entry Map",
        "name is $name, value is $value"
    )

    companion object {

    }
}
