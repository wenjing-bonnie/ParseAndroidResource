package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils
import kotlin.experimental.and

/**
 * create by wenjing.liu at 2023/7/29
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#1421
 * A collection of resource entries for a particular resource data
 * type.
 *
 * If the flag FLAG_SPARSE is not set in `flags`, then this struct is
 * followed by an array of uint32_t defining the resource
 * values, corresponding to the array of type strings in the
 * ResTable_package::typeStrings string block. Each of these hold an
 * index from entriesStart; a value of NO_ENTRY means that entry is
 * not defined.
 *
 * If the flag FLAG_SPARSE is set in `flags`, then this struct is followed
 * by an array of ResTable_sparseTypeEntry defining only the entries that
 * have values for this type. Each entry is sorted by their entry ID such
 * that a binary search can be performed over the entries. The ID and offset
 * are encoded in a uint32_t. See ResTabe_sparseTypeEntry.
 *
 * There may be multiple of these chunks for a particular resource type,
 * supply different configuration variations for the resource values of
 * that type.
 *
 * It would be nice to have an additional ordered index of entries, so
 * we can do a binary search if trying to find a resource by string name.
 *  struct ResTable_type
 *  {
 *   struct ResChunk_header header;
 *   enum {
 *       NO_ENTRY = 0xFFFFFFFF
 *   };
 *   // The type identifier this chunk is holding.  Type IDs start
 *   // at 1 (corresponding to the value of the type bits in a
 *    // resource identifier).  0 is invalid.
 *   uint8_t id;
 *    enum {
 *      // If set, the entry is sparse, and encodes both the entry ID and offset into each entry,
 *      // and a binary search is used to find the key. Only available on platforms >= O.
 *      // Mark any types that use this with a v26 qualifier to prevent runtime issues on older
 *      // platforms.
 *      FLAG_SPARSE = 0x01,
 *    };
 *    uint8_t flags;
 *    // Must be 0.
 *    uint16_t reserved;

 *    // Number of uint32_t entry indices that follow.
 *    uint32_t entryCount;
 *   // Offset from header where ResTable_entry data starts.
 *    uint32_t entriesStart;
 *    // Configuration this collection of entries is designed for. This must always be last.
 *    ResTable_config config;
 *   };
 */
class ResTypeInfoSixChunk(
    override val inputResourceByteArray: ByteArray,
    override val startOffset: Int,
    /**
     * all resource type list: [attr, drawable, layout, anim, raw, color, dimen, string, style, id]
     */
    val typeStringList: MutableList<String> = mutableListOf()
) : ChunkParseOperator {

    var id: Int = -1
    var res0: Byte = -1
    var res1: Short = -1
    var entryCount: Int = -1
    var entriesStart: Int = -1

    // TODO rename
    var resConfig: String = ""


    override val header: ResChunkHeader
        get() = ResChunkHeader(resArrayStartZeroOffset)

    override val chunkEndOffset: Int
        get() = header.size

    override fun chunkParseOperator(): ChunkParseOperator {
        var attributeOffset = header.chunkEndOffset
        var attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            ResTypeSpecAndTypeInfoSixChunk.ID_BYTE
        )
        id = attributeByteArray?.let { idArray ->
            (idArray[0] and 0xFF.toByte()).toInt()
        } ?: -1

        // this res0 is standing by, it is 0
        attributeOffset += ResTypeSpecAndTypeInfoSixChunk.ID_BYTE
        attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            ResTypeSpecAndTypeInfoSixChunk.RES0_BYTE
        )
        res0 = attributeByteArray?.let { res0Array ->
            (res0Array[0] and 0xFF.toByte())
        } ?: -1

        // this res1 is standing by, it is 0
        attributeOffset += ResTypeSpecAndTypeInfoSixChunk.RES0_BYTE
        attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            ResTypeSpecAndTypeInfoSixChunk.RES1_BYTE
        )
        res1 = Utils.byte2Short(attributeByteArray)

        attributeOffset += ResTypeSpecAndTypeInfoSixChunk.RES1_BYTE
        attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            ResTypeSpecAndTypeInfoSixChunk.ENTRY_COUNT_BYTE
        )
        entryCount = Utils.byte2Int(attributeByteArray)

        attributeOffset += ResTypeSpecAndTypeInfoSixChunk.ENTRY_COUNT_BYTE
        attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset,
            attributeOffset,
            ResTypeSpecAndTypeInfoSixChunk.ENTRIES_START_BYTE
        )
        entriesStart = Utils.byte2Int(attributeByteArray)
        // ResTable_config
        attributeOffset += ResTypeSpecAndTypeInfoSixChunk.ENTRIES_START_BYTE
        val resTableConfig =
            ResTypeInfoTableConfigChunkChild(resArrayStartZeroOffset, attributeOffset)
        resConfig = resTableConfig.toString()
        // TODO [attr, drawable, layout, anim, raw, color, dimen, string, style, id]
        Logger.debug("All resource type $typeStringList")
        return this
    }

    override fun chunkProperty() = ChunkProperty.CHUNK


    override fun toString(): String =
        "Part6: ->Type info header is ${header}\n" +
                "          id is $id, res0 is $res0, res1 is $res1,  entryCount is $entryCount, entriesStart is $entriesStart \n" +
                "          $resConfig" +
                "\nPart6: -> End..."


}
