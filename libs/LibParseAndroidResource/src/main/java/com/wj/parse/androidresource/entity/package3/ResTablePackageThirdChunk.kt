package com.wj.parse.androidresource.entity.package3

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

    override val chunkEndOffset: Int
        get() = TODO("Not yet implemented")

    override fun chunkParseOperator(): ChunkParseOperator {
        TODO("Not yet implemented")
    }

    override fun chunkProperty(): ChunkProperty = ChunkProperty.CHUNK
}