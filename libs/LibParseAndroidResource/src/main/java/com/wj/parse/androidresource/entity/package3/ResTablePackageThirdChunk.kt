package com.wj.parse.androidresource.entity.package3

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.stringpool2.ResStringPoolSecondChunk
import com.wj.parse.androidresource.entity.table1.ResourceTableHeaderFirstChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty

/**
 * create by wenjing.liu at 2023/7/22
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
class ResTablePackageThirdChunk(
    /**
     * whole resource byte array
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * The [startOffset] of this chunk is [ResourceTableHeaderFirstChunk.chunkEndOffset] + [ResStringPoolSecondChunk.chunkEndOffset]
     */
    override val startOffset: Int
) : ChunkParseOperator {

    var id: Int = -1
    var name: CharArray = CharArray(128)
    var typeStrings: Int = -1
    var lastPublicType: Int = -1
    var keyStrings: Int = -1
    var lastPublicKey: Int = -1

    override val chunkEndOffset: Int
        get() = TODO("Not yet implemented")

    override val header: ResChunkHeader
        get() = ResChunkHeader(resArrayStartZeroOffset)

    override fun chunkParseOperator(): ChunkParseOperator {
        var attributeStartOffset = 0
        attributeStartOffset += header.chunkEndOffset

        return this
    }

    override fun chunkProperty(): ChunkProperty = ChunkProperty.CHUNK
}