package com.wj.parse.androidresource.entity.typespec6.type

/**
 * create by wenjing.liu at 2023/8/26
 *
 */
data class Res(
    val packageId: Int,
    val resTypeSpecId: Int,
    val entryId: Int,
    val key: String = ""
) : Comparable<Res> {

    val id: String
        get() {
            return "0x${Integer.toHexString(idInt)}"
        }
    private var idInt: Int = -1

    init {
        idInt = getResourceIdInt()
    }

    /**
     * Simple resource
     */
    var value: String = ""

    /**
     * if the resource is complex resource, holding a set of name/value mappings.  there will be an array of attributes
     *  TODO only add string
     */
    var attributes: MutableList<String> = mutableListOf()

    override fun compareTo(other: Res): Int =
        when {
            other == null -> {
                1
            }

            other.key != null -> {
                key.compareTo(other.key)
            }

            idInt > other.idInt -> {
                1
            }

            idInt == other.idInt -> {
                0
            }

            else -> {
                -1
            }
        }

    override fun toString() =
        if (value.isNotBlank()) {
            "id: $id, key: $key, value: $value"
        } else {
            "id: $id, key: $key, value: $attributes"
        }


    /**
     * resourceId is structured as: 0xpptteeee,
     *  where pp is the package index, tt is the type index in that
     *  package, and eeee is the entry index in that type.  The package
     *  and type values start at 1 for the first item, to help catch cases
     *  where they have not been supplied.
     * | packageId | resTypeSpecId | entryId |
     */
    private fun getResourceIdInt() =
        packageId shl 24 or (resTypeSpecId shl 16) or (entryId and 0xFFFF)
}