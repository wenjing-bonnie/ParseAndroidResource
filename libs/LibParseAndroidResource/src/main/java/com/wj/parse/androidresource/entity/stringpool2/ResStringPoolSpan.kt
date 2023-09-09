package com.wj.parse.androidresource.entity.stringpool2

/**
 * create by wenjing.liu at 2023/8/30
 *
 *  /**
 * This structure defines a span of style information associated with
 * a string in the pool.
 * */
 *  struct ResStringPool_span
 * {
 *       enum {
 *       END = 0xFFFFFFFF
 *   };
 *   // This is the name of the span -- that is, the name of the XML
 * // tag that defined it.  The special value END (0xFFFFFFFF) indicates
 * // the end of an array of spans.
 * ResStringPool_ref name;
 * // The range of characters in the string that this span applies to.
 * uint32_t firstChar, lastChar;
 * };
 */
data class ResStringPoolSpan(
    val name: ResStringPoolRef,
    val firstChar: Int,
    val lastChar: Int
) {
    companion object {
        const val RANGE_IN_BYTE = 4
        const val SIZE_IN_BYTE = ResStringPoolRef.SIZE_IN_BYTE + RANGE_IN_BYTE * 2
    }
}