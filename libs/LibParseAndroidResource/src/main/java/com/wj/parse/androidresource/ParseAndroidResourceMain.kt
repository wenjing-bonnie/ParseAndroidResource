package com.wj.parse.androidresource

import com.wj.parse.androidresource.parse.ParseResourceChain
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipFile

/**
 * https://developer.android.com/guide/topics/resources/string-resource
 * https://docs.fileformat.com/programming/arsc/
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h
 * https://www.jianshu.com/p/b04e5b4806a7?utm_campaign=maleskine...&utm_content=note&utm_medium=seo_notes&utm_source=recommendation
 */
fun main(args: Array<String>) {
    val resourcePath =
         "libs/LibParseAndroidResource/src/main/resources/resources.arsc"
    ParseResourceChain().startParseResource(resourcePath)
}
