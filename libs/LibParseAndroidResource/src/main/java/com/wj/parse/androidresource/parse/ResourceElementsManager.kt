package com.wj.parse.androidresource.parse

import com.wj.parse.androidresource.entity.typespec6.Res
import com.wj.parse.androidresource.utils.Logger

class ResourceElementsManager {

    var elementsMap = mutableMapOf<String, MutableList<Res>>()

    fun sortResourceElements(res: Res, resourceType: String) {
        if (elementsMap.containsKey(resourceType)) {
            elementsMap[resourceType]?.add(res)
        } else {
            val elements = mutableListOf<Res>()
            elements.add(res)
            elementsMap[resourceType] = elements
        }
//        if (resourceType.equals("drawable")){
//            Logger.debug("size is "+elementsMap.get("drawable")?.size)
//        }
    }

    fun sort() {
        elementsMap.map {
            it.value.sort()
        }
    }

    override fun toString() =
        "--------------------------------\n" +
                "All resource elements are listed:\n" +
                "--------------------------------\n" +
                elementsMap.mapValues {
                    formatResourceToString(it.value, it.key)
                } +
                "--------------------------------\n"

    private fun formatResourceToString(res: MutableList<Res>, type: String) =
        "${res.size} numbers of ${type}:\n$res\n"
    //"${res.size} numbers of ${type}:\n"

    enum class ResourceType(val type: String) {
        ATTR("attr"),
        DRAWABLE("drawable"),
        LAYOUT("layout"),
        ANIM("anim"),
        RAW("raw"),
        COLOR("color"),
        DIMEN("dimen"),
        STRING("string"),
        STYLE("style"),
        ID("id")
    }
}