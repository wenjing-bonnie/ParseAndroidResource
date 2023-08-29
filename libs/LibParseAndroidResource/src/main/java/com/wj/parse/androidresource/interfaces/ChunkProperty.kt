package com.wj.parse.androidresource.interfaces

enum class ChunkProperty {
    CHUNK,
    /**
     * The header for this chunk area
     */
    CHUNK_AREA_HEADER,
    CHUNK_CHILD,

    /**
     *
     */
    CHUNK_CHILD_CHILD,

    /**
     * the common header of every chunk as describe by the ResChunk_header
     */
    CHUNK_HEADER
}