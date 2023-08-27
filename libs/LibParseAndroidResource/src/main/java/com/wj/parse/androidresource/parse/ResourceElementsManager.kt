package com.wj.parse.androidresource.parse

import com.wj.parse.androidresource.entity.typespec6.Res

class ResourceElementsManager {

    private var _elementsMap = mutableMapOf<String, MutableList<Res>>()

    /**
     * all the resource elements
     */
    val sortedElementsMap: Map<String, MutableList<Res>>
        get() {
            //elementsMap.entries.sortedBy { it.value }.associateBy({ it.key }, { it.value })
            // Logger.debug(elementsMap.get("attr").toString())
            _elementsMap.mapValues {
                it.value.sort()
            }
            // Logger.debug(elementsMap.get("attr").toString())
            return _elementsMap
        }

    fun storeResourceElements(res: Res, resourceType: String) {
        if (_elementsMap.containsKey(resourceType)) {
            _elementsMap[resourceType]?.add(res)
        } else {
            val elements = mutableListOf<Res>()
            elements.add(res)
            _elementsMap[resourceType] = elements
        }
    }

    // TODO
    override fun toString() =
        "--------------------------------\n" +
                "All resource elements are listed:\n" +
                "--------------------------------\n" +
                _elementsMap.map {
                    formatResourceToString(it.key, it.value)
                } +
                "\n--------------------------------\n"
    private fun formatResourceToString(type: String, res: MutableList<Res>) =
        // "${res.size} numbers of ${type}:\n$res\n"
        "${res.size} numbers of ${type};\n"
}