package com.wj.parse.androidresource.parse

import com.wj.parse.androidresource.entity.stringpool2.ResGlobalStringPoolChunk
import com.wj.parse.androidresource.entity.stringpool4.ResTypeStringPoolChunk
import com.wj.parse.androidresource.entity.stringpool5.ResKeyStringsPoolChunk
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

    override fun toString() =
        "\n------------------------------------------------\n" +
                "| Part resource elements are listed:           |\n" +
                "| type is from ${ResTypeStringPoolChunk::class.simpleName}      |\n" +
                "| key is from ${ResKeyStringsPoolChunk::class.simpleName}       |\n" +
                "| value is from ${ResGlobalStringPoolChunk::class.simpleName} |\n"+
                "------------------------------------------------\n" +
                _elementsMap.map {
                    formatResourceToString(it.key, it.value)
                } +
                "\n------------------------------------------------\n"

    private fun formatResourceToString(type: String, res: MutableList<Res>) =
        // "${res.size} numbers of ${type}:\n$res\n"
        "\n${res.size} numbers of ${type}:" +
                "${
                    res.joinToString(
                        prefix = "[",
                        limit = 10,
                        truncated = "...",
                        postfix = "]",
                        separator = "\n"
                    )
                }\n"
}