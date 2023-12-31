package com.wj.parse.androidresource.entity.typespec6.typespec

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk.Companion.ENTRY_COUNT_BYTE
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk.Companion.ID_BYTE
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk.Companion.RES0_BYTE
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk.Companion.RES1_BYTE
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils
import kotlin.experimental.and

/**
 * create by wenjing.liu at 2023/7/29
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#1369
 * A specification of the resources defined by a particular type.
 * There should be one of these chunks for each resource type.
 *
 * This structure is followed by an array of integers providing the set of
 * configuration change flags (ResTable_config::CONFIG_*) that have multiple
 * resources for that configuration.  In addition, the high bit is set if that
 * resource has been made public.
 *  struct ResTable_typeSpec
 *   {
 *     struct ResChunk_header header;
 *       // The type identifier this chunk is holding.  Type IDs start
 *       // at 1 (corresponding to the value of the type bits in a
 *       // resource identifier).  0 is invalid.
 *     uint8_t id;
 *     // Must be 0.
 *     uint8_t res0;
 *     // Must be 0.
 *     uint16_t res1;
 *     // Number of uint32_t entry configuration masks that follow.
 *     uint32_t entryCount;
 *     enum : uint32_t {
 *         // Additional flag indicating an entry is public.
 *       SPEC_PUBLIC = 0x40000000u,
 *        // Additional flag indicating the resource id for this resource may change in a future
 *        // build. If this flag is set, the SPEC_PUBLIC flag is also set since the resource must be
 *       // public to be exposed as an API to other applications.
 *       SPEC_STAGED_API = 0x20000000u,
 *     };
 *    };
 */
class ResTableTypeSpecChildChunk(
    override val inputResourceByteArray: ByteArray,
    override val startOffset: Int,
    override val childPosition: Int
) : ChunkParseOperator {
    /**
     *    // The type identifier this chunk is holding.  Type IDs start
     *    // at 1 (corresponding to the value of the type bits in a
     *    // resource identifier).  0 is invalid.
     *    uint8_t id;
     */
    var id: Int = -1

    /**
     *     // Must be 0.
     *     uint8_t res0;
     */
    var res0: Byte = -1

    /**
     *     // Must be 0.
     *     uint16_t res1;
     */
    var res1: Short = -1

    /**
     *     // Number of uint32_t entry configuration masks that follow.
     *     uint32_t entryCount;
     */
    var entryCount: Int = -1

    override val header: ResChunkHeader
        get() = ResChunkHeader(resArrayStartZeroOffset)

    init {
        checkChunkAttributes()
        chunkParseOperator()
    }

    override val endOffset: Int
        get() = startOffset + header.size

    override val position: Int
        get() = ResTableTypeSpecAndTypeChunk.POSITION

    override fun chunkParseOperator(): ChunkParseOperator {
        var attributeOffset = header.endOffset
        var attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, ID_BYTE)
        id = attributeByteArray?.let { idArray ->
            (idArray[0] and 0xFF.toByte()).toInt()
        } ?: -1

        // this res0 is standing by, it is 0
        attributeOffset += ID_BYTE
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, RES0_BYTE)
        res0 = attributeByteArray?.let { res0Array ->
            (res0Array[0] and 0xFF.toByte())
        } ?: -1

        // this res1 is standing by, it is 0
        attributeOffset += RES0_BYTE
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, RES1_BYTE)
        res1 = Utils.byte2Short(attributeByteArray)

        attributeOffset += RES1_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, ENTRY_COUNT_BYTE)
        entryCount = Utils.byte2Int(attributeByteArray)

        return this
    }

    override val chunkProperty
        get() = ChunkProperty.CHUNK_AREA_CHILD


    override fun toString(): String =
        formatToString(
            chunkName = "${Logger.FIRST_LEVEL} ResTable_typeSpec, the next is ResTable_type ${Logger.FIRST_LEVEL}",
            "$header",
            "id is $id, res0 is $res0, res1 is $res1,  entryCount is $entryCount"
        )

    companion object {
        /**
         * // Additional flag indicating an entry is public.
         */
        private const val SPEC_PUBLIC = 0x40000000

        /**
         * // Additional flag indicating the resource id for this resource may change in a future
         *  // build. If this flag is set, the SPEC_PUBLIC flag is also set since the resource must be
         *  // public to be exposed as an API to other applications.
         */
        private const val SPEC_STAGED_API = 0x20000000u
    }

}
