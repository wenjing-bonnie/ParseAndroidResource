package com.wj.parse.androidresource.entity.typespec6.type.complex

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolRef
import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolChunk
import com.wj.parse.androidresource.entity.typespec6.type.Res
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk
import com.wj.parse.androidresource.entity.typespec6.type.ResTableTypeEntryChildChildChunk.Flags
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils

/**
 * create by wenjing.liu at 2023/8/25
 **
 * Extended form of a ResTable_entry for map entries, defining a parent map
 * resource from which to inherit values.
 * TODO can be extends from????
 * struct ResTable_map_entry : public ResTable_entry
 * {
 *    // Resource identifier of the parent mapping, or 0 if there is none.
 *    // This is always treated as a TYPE_DYNAMIC_REFERENCE.
 *    ResTable_ref parent;
 *    // Number of name/value pairs that follow for FLAG_COMPLEX.
 *    uint32_t count;
 * };
 */
class ResTableTypeMapEntityChildChildChunk(
    /**
     * the string pool chunk byte array which index has started from 0 for this child chunk
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
    private val resourceKey: String,
    /**
     * Google Pool String. It comes from [ResGlobalStringPoolChunk.resStringPoolRefOffset.globalStringList]
     */
    private val globalStringList: MutableList<String>,
    private val res: Res
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
    // Resource identifier of the parent mapping, or 0 if there is none.
    // This is always treated as a TYPE_DYNAMIC_REFERENCE.
    private lateinit var parent: ResTableRef
    // Number of name/value pairs that follow for FLAG_COMPLEX.
    var count: Int = 0
    lateinit var tableTypeMapChunkChild: ResTableTypeMapChildChildChunk

    init {
        checkChunkAttributes()
        chunkParseOperator()
    }

    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header, because this is a child chunk without header.")
            null
        }

    override val endOffset: Int
        get() = SIZE_IN_BYTE + FLAGS_IN_BYTE + KEY_IN_BYTE + ResStringPoolRef.SIZE_IN_BYTE + COUNT_IN_BYTE + if (::tableTypeMapChunkChild.isInitialized) {
            tableTypeMapChunkChild.endOffset * count
        } else {
            0
        }

    override val childPosition: Int
        get() = ChunkParseOperator.CHILD_CHILD_POSITION

    override val position: Int
        get() = ResTableTypeSpecAndTypeChunk.POSITION

    override val chunkProperty
        get() = ChunkProperty.CHUNK_AREA_CHILD_CHILD

    override fun chunkParseOperator(): ChunkParseOperator {
        var attributeOffset = 0
        var attributeArrayByte = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            SIZE_IN_BYTE
        )
        // 0 - 2
        size = Utils.byte2Short(attributeArrayByte)

        attributeOffset += SIZE_IN_BYTE
        attributeArrayByte = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            FLAGS_IN_BYTE
        )
        // 2 - 2
        flags = Utils.byte2Short(attributeArrayByte)

        attributeOffset += FLAGS_IN_BYTE
        attributeArrayByte = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            KEY_IN_BYTE
        )
        // 4 - 4
        key = ResStringPoolRef(Utils.byte2Int(attributeArrayByte))

        attributeOffset += KEY_IN_BYTE
        attributeArrayByte = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            KEY_IN_BYTE
        )
        // 8 - 4
        parent = ResTableRef(Utils.byte2Int(attributeArrayByte))

        attributeOffset += ResTableRef.SIZE_IN_BYTE
        attributeArrayByte = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            COUNT_IN_BYTE
        )
        // 12 - 4
        count = Utils.byte2Int(attributeArrayByte)

        attributeOffset += COUNT_IN_BYTE
        mapChunkChildParseOperator(attributeOffset)
        return this
    }

    private fun mapChunkChildParseOperator(attributeOffset: Int) {
        var mapOffset = attributeOffset
        // TODO sometimes the count is 0 ??? for example,
        //  size is 16, flags is FLAG_COMPLEX, key is ResStringPoolRef(index=1056), resourceKey is AppBaseTheme
        for (index in 0 until count) {
            tableTypeMapChunkChild =
                ResTableTypeMapChildChildChunk(resArrayStartZeroOffset, mapOffset, globalStringList)
            res.value = tableTypeMapChunkChild.value.dataString
            mapOffset += tableTypeMapChunkChild.endOffset * index
            // res.value.indexOf("<") >= 0
            // value=<0xFFFFFFFF, type 0x00>
            if (tableTypeMapChunkChild.value.invalidDataType(res.value)) {
                continue
            }
        }
    }

    override fun toString(): String =
        formatToString(
            chunkName = "Res Table Map Entity",
            "size is $size, flags is ${Flags.valueOf(flags)}, key is $key, resourceKey is $resourceKey",
            "parent is ${
                if (::parent.isInitialized) {
                    "$parent"
                } else {
                    "not Initialized"
                }
            },  count is $count",
            if (::tableTypeMapChunkChild.isInitialized) {
                tableTypeMapChunkChild.toString()
            } else {
                // sometimes the count is 0, so the tableTypeMapChunkChild isn't initialized
                ""
            }
        )

    companion object {
        private const val SIZE_IN_BYTE = 2
        private const val FLAGS_IN_BYTE = 2
        private const val KEY_IN_BYTE = 4
        private const val COUNT_IN_BYTE = 4
    }

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

        override fun toString() =
            // TODO optimized
            "ident:0x${Utils.bytesToHexString(Utils.int2Byte(ident))}"
    }
}
