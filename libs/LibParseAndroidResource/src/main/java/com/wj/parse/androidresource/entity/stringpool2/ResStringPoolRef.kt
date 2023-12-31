package com.wj.parse.androidresource.entity.stringpool2

/**
 * Reference to a string in a string pool.
 * struct ResStringPool_ref
 * {
 *    // Index into the string pool table (uint32_t-offset from the indices
 *    // immediately after ResStringPool_header) at which to find the location
 *    // of the string data in the pool.
 *    uint32_t index;
 * };
 */
data class ResStringPoolRef(val index: Int) {
    companion object{
        const val SIZE_IN_BYTE = 4
    }
}
