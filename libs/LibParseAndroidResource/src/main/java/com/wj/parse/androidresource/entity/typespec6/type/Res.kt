package com.wj.parse.androidresource.entity.typespec6.type

/**
 * create by wenjing.liu at 2023/8/26
 * TODO confirm this struct
 */
data class Res(
    val id: Int = -1,
    val key: String = ""
) : Comparable<Res> {
    var value: String = ""

    /**
     * if the resource is complex resource, there will be an array of attribute resources
     */
    lateinit var attributes: MutableList<Res>

    override fun compareTo(other: Res): Int =
        when {
            other == null -> {
                1
            }

            other.key != null -> {
                key.compareTo(other.key)
            }

            id > other.id -> {
                1
            }

            id == other.id -> {
                0
            }

            else -> {
                -1
            }
        }

    override fun toString() = "id: $id, key: $key, value: $value"
}