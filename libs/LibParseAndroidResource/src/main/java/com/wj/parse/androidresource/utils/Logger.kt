package com.wj.parse.androidresource.utils

import org.slf4j.LoggerFactory


object Logger {

    fun debug(info: String) {
        kotlin.run {
            TAG.debug(info)
            println(info)
        }.takeIf { DEBUG }
    }

    fun error(info: String) {
        kotlin.run {
            TAG.error(info)
            println(info)
        }.takeIf { DEBUG }
    }

    private const val DEBUG = true
    private val TAG = LoggerFactory.getLogger("ParseAndroidResource")
    const val TAG_SPACE = "       "
    const val END_TAG_START = ">>>>>>"
    const val END_TAG_END= "<<<<<<"
    const val TITLE_TAG_START = ">> "
    const val TITLE_TAG_END = " <<"
    const val FIRST_LEVEL = "*"
    const val SECOND_LEVEL = "**"
    const val THIRD_LEVEL = "***"
    const val FOURTH_LEVEL = "****"
}
