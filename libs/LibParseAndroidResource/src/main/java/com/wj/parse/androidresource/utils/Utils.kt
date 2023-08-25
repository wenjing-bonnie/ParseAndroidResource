package com.wj.parse.androidresource.utils


object Utils {

    /**
     * @param src
     * @param start the start offset of this data
     */
    fun copyByte(src: ByteArray?, start: Int): ByteArray? = src?.let {
        copyByte(src, start, it.size - start)
    } ?: null

    /**
     * @param src
     * @param start the start offset of this data
     * @param len the length of this data
     */
    fun copyByte(src: ByteArray?, start: Int, len: Int): ByteArray? =
        when {
            src == null ||
                    start < 0 ||
                    len <= 0 ||
                    start > src.size ||
                    start + len > src.size -> null

            else -> {
                val resultByte = ByteArray(len)
                for (i in 0 until len) {
                    resultByte[i] = src[i + start]
                }
                resultByte
            }
        }

    fun byte2Short(srcArray: ByteArray?): Short =
        srcArray?.let {
            val s0: Short = (srcArray[0].toInt() and 0xff).toShort()
            var s1: Short = (srcArray[1].toInt() and 0xff).toShort()
            s1 = (s1.toInt() shl 8).toShort()
            (s0.toInt() or s1.toInt()).toShort()
        } ?: run {
            0
        }

    fun byte2Int(srcArray: ByteArray?): Int = srcArray?.let {
        (srcArray[0].toInt() and 0xff or (srcArray[1].toInt() shl 8 and 0xff00)
                or (srcArray[2].toInt() shl 24 ushr 8) or (srcArray[3].toInt() shl 24))
    } ?: run {
        0
    }

    fun bytesToHexString(srcArray: ByteArray?): String? = srcArray?.let {
        when {
            srcArray.isEmpty() -> null
            else -> {
                val stringBuilder = StringBuilder()
                for (i in srcArray.indices) {
                    val v = srcArray[i].toInt() and 0xFF
                    val hv = Integer.toHexString(v)
                    if (hv.length < 2) {
                        stringBuilder.append(0)
                    }
                    stringBuilder.append("$hv ")
                }
                stringBuilder.toString()
            }
        }
    } ?: run {
        null
    }

    fun byte2StringFilterStringNull(srcArray: ByteArray?): String? =
        srcArray?.let { bytes ->
            String(
                bytes.filter {
                    it.toInt() != 0
                }.toByteArray()
            )
        } ?: null


    fun int2Byte(integer: Int): ByteArray? {
        val byteNum =
            (40 - Integer.numberOfLeadingZeros(if (integer < 0) integer.inv() else integer)) / 8
        val byteArray = ByteArray(4)
        for (n in 0 until byteNum) byteArray[3 - n] = (integer ushr n * 8).toByte()
        return byteArray
    }
}