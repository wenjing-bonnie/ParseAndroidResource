package com.wj.parse.androidresource.entity.typespec6.type

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.entity.package3.ResTablePackageChunk
import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolChunk
import com.wj.parse.androidresource.entity.typespec6.type.complex.ResTableTypeMapEntityChildChildChunk
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk
import com.wj.parse.androidresource.entity.typespec6.ResTableTypeSpecAndTypeChunk.Companion.ENTRY_OFFSET_BYTE
import com.wj.parse.androidresource.entity.typespec6.type.simple.ResTableTypeValueChildChildChunk
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.parse.ResourceElementsManager
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
 *   // resource identifier).  0 is invalid.
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
 *
 *  [ResTableTypeEntryChildChildChunk] is a entry in the type pool, it has a flag which is used to define this entity is complex or simple
 * if it is a complex resource, next parse [ResTableTypeMapEntityChildChildChunk], otherwise, [ResTableTypeValueChildChildChunk]
 */
class ResTableTypeChildChunk(
    override val inputResourceByteArray: ByteArray,
    override val startOffset: Int,
    override val childPosition: Int,
    /**
     * Google Pool String. It comes from [ResGlobalStringPoolChunk.resStringPoolRefOffset.globalStringList]
     */
    private val globalStringList: MutableList<String>,
    /**
     * all resource type list: [attr, drawable, layout, anim, raw, color, dimen, string, style, id]
     */
    private val resTypeStringList: MutableList<String>,
    /**
     * all resource key list
     */
    private val resKeyStringList: MutableList<String>,
    /**
     * [ResTablePackageChunk.id]
     */
    private val packageId: Int,
    /**
     * [ResTableTypeSpecChildChunk.id]
     */
    private val resTypeSpecId: Int,
    private val resourceElementsManager: ResourceElementsManager = ResourceElementsManager()
) : ChunkParseOperator {
    /**
     *   // The type identifier this chunk is holding.  Type IDs start
     *   // at 1 (corresponding to the value of the type bits in a
     *   // resource identifier).  0 is invalid.
     *   uint8_t id;
     */
    var id: Int = -1

    /**
     *    enum {
     *      // If set, the entry is sparse, and encodes both the entry ID and offset into each entry,
     *      // and a binary search is used to find the key. Only available on platforms >= O.
     *      // Mark any types that use this with a v26 qualifier to prevent runtime issues on older
     *      // platforms.
     *      FLAG_SPARSE = 0x01,
     *    };
     */
    var flags: Byte = -1

    /**
     *    // Must be 0.
     *    uint16_t reserved;
     */
    var reserved: Short = -1

    /**
     *
     * // Number of uint32_t entry indices that follow.
     * uint32_t entryCount;
     */
    var entryCount: Int = -1

    /**
     * // Offset from header where ResTable_entry data starts.
     * uint32_t entriesStart;
     */
    var entriesStart: Int = -1

    /**
     *  // Configuration this collection of entries is designed for. This must always be last.
     *  ResTable_config config;
     */
    lateinit var config: ResTableConfigChildChildChunk

    // array of the entry offset
    val entryOffsets = mutableListOf<Int>()

    // ResTable_entry
    lateinit var entry: ResTableTypeEntryChildChildChunk

    /**
     * current resource type
     */
    lateinit var resourceTypeString: String

    init {
        checkChunkAttributes()
        chunkParseOperator()
    }

    override val header: ResChunkHeader
        get() = ResChunkHeader(resArrayStartZeroOffset)

    override val endOffset: Int
        get() = startOffset + header.size

    override val position: Int
        get() = ResTableTypeSpecAndTypeChunk.POSITION

    override fun chunkParseOperator(): ChunkParseOperator {
        var attributeOffset = header.endOffset

        /** id is behind the [ResChunkHeader]*/
        var attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            ResTableTypeSpecAndTypeChunk.ID_BYTE
        )
        id = attributeByteArray?.let { idArray ->
            (idArray[0] and 0xFF.toByte()).toInt()
        } ?: -1

        /** Next is the flags */
        // this res0 is standing by, it is 0
        attributeOffset += ResTableTypeSpecAndTypeChunk.ID_BYTE
        attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            ResTableTypeSpecAndTypeChunk.RES0_BYTE
        )
        flags = attributeByteArray?.let { res0Array ->
            (res0Array[0] and 0xFF.toByte())
        } ?: -1

        /** Next is the reserved */
        // this res1 is standing by, it is 0
        attributeOffset += ResTableTypeSpecAndTypeChunk.RES0_BYTE
        attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            ResTableTypeSpecAndTypeChunk.RES1_BYTE
        )
        reserved = Utils.byte2Short(attributeByteArray)

        /** Next is the entryCount */
        attributeOffset += ResTableTypeSpecAndTypeChunk.RES1_BYTE
        attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset, attributeOffset,
            ResTableTypeSpecAndTypeChunk.ENTRY_COUNT_BYTE
        )
        entryCount = Utils.byte2Int(attributeByteArray)

        /** Next is the entriesStart */
        attributeOffset += ResTableTypeSpecAndTypeChunk.ENTRY_COUNT_BYTE
        attributeByteArray = Utils.copyByte(
            resArrayStartZeroOffset,
            attributeOffset,
            ResTableTypeSpecAndTypeChunk.ENTRIES_START_BYTE
        )
        entriesStart = Utils.byte2Int(attributeByteArray)
        // ResTable_config
        attributeOffset += ResTableTypeSpecAndTypeChunk.ENTRIES_START_BYTE

        /** Next is the ResTable_config */
        // TODO why config.chunkEndOffset is 36, not 48?
        config =
            ResTableConfigChildChildChunk(resArrayStartZeroOffset, attributeOffset)
        // resourceTypeStringList: [attr, drawable, layout, anim, raw, color, dimen, string, style, id]

        /** Next is the ResTable_entry */
        val typeIndex = id - 1
        if (typeIndex >= resTypeStringList.size) {
            throw IllegalStateException("The id $id is wrong, can't find it in the $resTypeStringList")
        }
        // get the current resource type
        resourceTypeString = resTypeStringList[typeIndex]
        // get the entryCount
        // entryOffsets is right after the header
        for (index in 0 until entryCount) {
            attributeOffset = header.headerSize + index * ENTRY_OFFSET_BYTE
            attributeByteArray =
                Utils.copyByte(resArrayStartZeroOffset, attributeOffset, ENTRY_OFFSET_BYTE)
            val element = Utils.byte2Int(attributeByteArray)
            entryOffsets.add(element)
        }
        // Logger.debug(entries.toString())
        // next is ResTableEntry,
        /**
         * | -------------------------- header --------------------  headerSize ->|--- entryCount x 4 ->|<- entriesStart ------ |
         * |-- headerSize --|- 1 -|--- 3  ---|---- 4  ----|---- 4  ----- |---36 --|----              ---|                    ---|
         * |    header      |  id | reserved | entryCount | entriesStart | config | entry offset array  |  ResTable_entry array |
         * |-- headerSize --|- 1 -|--- 3  ---|---- 4  ----|---- 4  ----- |---36 --|----              ---|                    ---|
         */
        // Logger.debug("0 attributeOffset ${attributeOffset}")
        // Logger.debug("entriesStart is $entriesStart, headerSize is ${header.headerSize}, entryCount is $entryCount")
        for (index in 0 until entryCount) {
            // Logger.debug(" attributeOffset ${attributeOffset}, ${entriesStart+entries[index]} , ${+entries[index]} ")
            attributeOffset = entriesStart + entryOffsets[index]
            // Logger.debug("====== $index resKeyStringList is ${resKeyStringList.size}")
            // it seems to be a header
            entry = ResTableTypeEntryChildChildChunk(
                resArrayStartZeroOffset,
                attributeOffset,
                resKeyStringList
            )

            // Logger.debug("$index entry is $entry")
            // 1.the id is generated by packageId, resTypeSpecId and index;
            // 2.find the key from entry.resKeyString
            val res = Res(
                packageId = packageId,
                resTypeSpecId = resTypeSpecId,
                entryId = index,
                key = entry.resKeyString
            )
            // 3.find the value from next body part.
            when (entry.flags) {
                // If set FLAG_COMPLEX, this is a complex entry, holding a set of name/value
                // mappings. It is followed by an array of ResTable_map structures.
                // [attr, style] is a complex entry
                ResTableTypeEntryChildChildChunk.Flags.FLAG_COMPLEX.value -> {
//                    if (index == 0)
//                        Logger.debug("$resourceTypeString is a complex entry")
                    //   Logger.debug(" attributeOffset is $attributeOffset")
                    val complexMapEntity = ResTableTypeMapEntityChildChildChunk(
                        resArrayStartZeroOffset,
                        attributeOffset,
                        entry.resKeyString,
                        globalStringList = globalStringList,
                        res = res
                    )
//                    if(resourceTypeString.equals("drawable")){
//                       Logger.debug("$index map is $complexMapEntity")
//                    }
                    // it is replaced with "attributeOffset = entriesStart + entryOffsets[index]"
                    // attributeOffset += complexMapEntity.endOffset
                }

                else -> {
                    // Logger.debug("$index == resourceId is $resourceId, entry is $entry")
                    // [drawable, layout, anim, raw, color, dimen, string, id] is simple resource type
//                    if (index == 0)
//                        Logger.debug("$resourceTypeString is a simple entry")
                    val notComplexEntity = ResTableTypeValueChildChildChunk(
                        resArrayStartZeroOffset,
                        attributeOffset + entry.endOffset,
                        globalStringList
                    )
                    res.value = notComplexEntity.dataString
                    // it is replaced with "attributeOffset = entriesStart + entryOffsets[index]"
                    // attributeOffset += entry.endOffset + notComplexEntity.endOffset
//                    Logger.debug("$index not complex map is $notComplexEntity")

                    if (notComplexEntity.invalidDataType(res.value)) {
                        // res.value.indexOf("<") >= 0
                        /** if this value is invalid, don't add this [Res] to [ResourceElementsManager._elementsMap] */
                        // Logger.debug("invalid entry offset ${entries[index]}")
                        continue
                    }
//                    if(resourceTypeString == "drawable"){
//                        Logger.debug("$index $resourceTypeString($entryCount) value is $value, res is $res")
//                    }
                }
            }
//            if (resourceTypeString.equals("drawable")) {
//                Logger.debug("$index, res is $res")
//            }
            resourceElementsManager.storeResourceElements(res = res, resourceTypeString)
        }
        // Logger.debug("$attributeOffset header.size is ${header.size}")
        return this
    }

    override val chunkProperty
        get() = ChunkProperty.CHUNK_AREA_CHILD

    override fun toString(): String =
        formatToString(
            chunkName = "${Logger.SECOND_LEVEL}  ResTable_type <$resourceTypeString> **",
            "$header",
            "id is $id, flags is $flags, reserved is $reserved,  entryCount is $entryCount, entriesStart is $entriesStart",
            "$config",
            "$entry"
        )
}
