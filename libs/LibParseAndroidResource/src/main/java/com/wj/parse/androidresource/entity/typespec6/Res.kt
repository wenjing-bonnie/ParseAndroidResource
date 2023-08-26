package com.wj.parse.androidresource.entity.typespec6

/**
 * create by wenjing.liu at 2023/8/26
 */
data class Res(
    val id: Int = -1,
    val name: String = ""
) : Comparable<Res> {
    var value: String = ""

    override fun compareTo(other: Res): Int =
        when {
            other == null -> {
                1
            }

            other.name != null -> {
                name.compareTo(other.name)
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

    override fun toString() = "name: $name, id: $id, value: $value"
}