package com.wj.parse.androidresource.interfaces

enum class ChunkType(val value: Int) {
    RES_NULL_TYPE(0x0000),
    RES_STRING_POOL_TYPE(0x0001),
    RES_TABLE_TYPE(0x0002),
    RES_XML_TYPE(0x0003),

    // Chunk types in RES_XML_TYPE
    RES_XML_FIRST_CHUNK_TYPE(0x0100),
    RES_XML_START_NAMESPACE_TYPE(0x0100),
    RES_XML_END_NAMESPACE_TYPE(0x0101),
    RES_XML_START_ELEMENT_TYPE(0x0102),
    RES_XML_END_ELEMENT_TYPE(0x0103),
    RES_XML_CDATA_TYPE(0x0104),
    RES_XML_LAST_CHUNK_TYPE(0x017f),

    // This contains a uint32_t array mapping strings in the string
    // pool back to resource identifiers.  It is optional.
    RES_XML_RESOURCE_MAP_TYPE(0x0180),

    // Chunk types in RES_TABLE_TYPE
    RES_TABLE_PACKAGE_TYPE(0x0200),
    RES_TABLE_TYPE_TYPE(0x0201),
    RES_TABLE_TYPE_SPEC_TYPE(0x0202),
    RES_TABLE_LIBRARY_TYPE(0x0203);

    override fun toString(): String {
        return value.toString()
    }

    companion object {
        fun valueOf(value: Int): String? = when (value) {
            RES_STRING_POOL_TYPE.value -> RES_STRING_POOL_TYPE.name
            RES_TABLE_TYPE.value -> RES_TABLE_TYPE.name
            RES_XML_TYPE.value -> RES_XML_TYPE.name
            RES_XML_FIRST_CHUNK_TYPE.value -> RES_XML_FIRST_CHUNK_TYPE.name
            RES_XML_START_NAMESPACE_TYPE.value -> RES_XML_START_NAMESPACE_TYPE.name
            RES_XML_END_NAMESPACE_TYPE.value -> RES_XML_END_NAMESPACE_TYPE.name
            RES_XML_START_ELEMENT_TYPE.value -> RES_XML_START_ELEMENT_TYPE.name
            RES_XML_END_ELEMENT_TYPE.value -> RES_XML_END_ELEMENT_TYPE.name
            RES_XML_CDATA_TYPE.value -> RES_XML_CDATA_TYPE.name
            RES_XML_LAST_CHUNK_TYPE.value -> RES_XML_LAST_CHUNK_TYPE.name
            RES_XML_RESOURCE_MAP_TYPE.value -> RES_XML_RESOURCE_MAP_TYPE.name
            RES_TABLE_PACKAGE_TYPE.value -> RES_TABLE_PACKAGE_TYPE.name
            RES_TABLE_TYPE_TYPE.value -> RES_TABLE_TYPE_TYPE.name
            RES_TABLE_TYPE_SPEC_TYPE.value -> RES_TABLE_TYPE_SPEC_TYPE.name
            RES_TABLE_LIBRARY_TYPE.value -> RES_TABLE_LIBRARY_TYPE.name
            else -> RES_NULL_TYPE.name
        }
    }
}