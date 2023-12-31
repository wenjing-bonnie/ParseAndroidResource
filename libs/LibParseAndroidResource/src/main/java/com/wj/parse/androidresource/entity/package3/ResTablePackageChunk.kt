package com.wj.parse.androidresource.entity.package3

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Utils

/**
 * create by wenjing.liu at 2023/7/22
 *
 * A collection of resource data types within a package.  Followed by
 * one or more ResTable_type and ResTable_typeSpec structures containing the
 * entry values for each resource type.
 *
 * struct ResTable_package
 * {
 *     struct ResChunk_header header;

 *     // If this is a base package, its ID.  Package IDs start
 *     // at 1 (corresponding to the value of the package bits in a
 *    // resource identifier).  0 means this is not a base package.
 *     uint32_t id;

 *     // Actual name of this package, \0-terminated.
 *     uint16_t name[128];

 *     // Offset to a ResStringPool_header defining the resource
 *     // type symbol table.  If zero, this package is inheriting from
 *    // another base package (overriding specific values in it).
 *    uint32_t typeStrings;

 *    // Last index into typeStrings that is for public use by others.
 *   uint32_t lastPublicType;

 *    // Offset to a ResStringPool_header defining the resource
 *    // key symbol table.  If zero, this package is inheriting from
 *    // another base package (overriding specific values in it).
 *    uint32_t keyStrings;

// Last index into keyStrings that is for public use by others.
 *   uint32_t lastPublicKey;

 *    uint32_t typeIdOffset;
 * };
 */

class ResTablePackageChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderChunk.endOffset] + [ResGlobalStringPoolChunk.endOffset]
     */
    override val startOffset: Int
) : ChunkParseOperator {

    var id: Int = -1
    var name: String = ""

    /**
     *  Offset to a ResStringPool_header defining the resource
     *  type symbol table.  If zero, this package is inheriting from
     *  another base package (overriding specific values in it).
     */
    var typeStrings: Int = -1
    var lastPublicType: Int = -1
    var keyStrings: Int = -1
    var lastPublicKey: Int = -1
    var typeIdOffset: Int = -1

    /**
     * this chunk is the part of header
     */
    override val endOffset: Int
        get() = startOffset + header.headerSize

    override val position: Int
        get() = 3

    override val childPosition: Int
        get() = 0

    /**
     * this header.headerSize is size of ResTable_package, from type to  lastPublicKey
     */
    override val header: ResChunkHeader
        get() = ResChunkHeader(resArrayStartZeroOffset)

    init {
        checkChunkAttributes()
        chunkParseOperator()
    }

    override fun chunkParseOperator(): ChunkParseOperator {
        var attributeStartOffset = header.endOffset
        var attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeStartOffset, ID_BYTE)
        id = Utils.byte2Int(attributeByteArray)

        attributeStartOffset += ID_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeStartOffset, NAME_BYTE)
        name = Utils.byte2StringFilterStringNull(attributeByteArray) ?: ""

        attributeStartOffset += NAME_BYTE
        attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset, attributeStartOffset,
            TYPE_STRINGS_BYTE
        )
        typeStrings = Utils.byte2Int(attributeByteArray)

        attributeStartOffset += TYPE_STRINGS_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeStartOffset, LAST_PUBLIC_TYPE_BYTE)
        lastPublicType = Utils.byte2Int(attributeByteArray)

        attributeStartOffset += LAST_PUBLIC_TYPE_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeStartOffset, KEY_STRING_BYTE)
        keyStrings = Utils.byte2Int(attributeByteArray)

        attributeStartOffset += KEY_STRING_BYTE
        // Logger.debug("attributeStartOffset is ${attributeStartOffset+startOffset}")
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeStartOffset, LAST_PUBLIC_KEY_BYTE)
        lastPublicKey = Utils.byte2Int(attributeByteArray)

        attributeStartOffset += LAST_PUBLIC_KEY_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeStartOffset, TYPE_ID_OFFSET)
        typeIdOffset = Utils.byte2Int(attributeByteArray)
        return this
    }

    override val chunkProperty
        get() = ChunkProperty.CHUNK_AREA_HEADER

    override fun toString(): String =
        formatToString(
            chunkName = "Resource Table Package(P3:ResTable_package)",
            "$header",
            "id is $id, name is $name, typeStrings is $typeStrings, lastPublicType is $lastPublicType, keyStrings is $keyStrings, lastPublicKey is $lastPublicKey, typeIdOffset is $typeIdOffset."
        )

    companion object {
        private const val ID_BYTE = 4
        private const val NAME_BYTE = 256
        private const val TYPE_STRINGS_BYTE = 4
        private const val LAST_PUBLIC_TYPE_BYTE = 4
        private const val KEY_STRING_BYTE = 4
        private const val LAST_PUBLIC_KEY_BYTE = 4
        private const val TYPE_ID_OFFSET = 4
    }
}
