package com.wj.parse.androidresource.interfaces

enum class ChunkProperty {

    /**
     * the common header of every chunk as describe by the [ResChunk_header]
     */
    CHUNK_HEADER,

    /**
     * The header of this chunk area
     * ResChunk_header
     * ResTable_package
     */
    CHUNK_AREA_HEADER,

    /**
     *  the whole chunk area
     */
    CHUNK_AREA,

    /**
     * this chunk can be reused, for example, string pool chunk
     */
    CHUNK_AREA_REUSED,


    /**
     * the child of this chunk area
     */
    CHUNK_AREA_CHILD,

    /**
     * the child of child of this chunk area
     */
    CHUNK_AREA_CHILD_CHILD,
}