@file: JvmName("MD5Utils")

package com.devmeng.skinlib.skin.utils

import com.devmeng.skinlib.skin.EMPTY
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

/**
 * Created by devmeng -> MHS
 * Date : 2022/5/31  20:56
 * Version : 1
 */
fun getMD5Code(info: String): String? {
    return try {
        val md5 = MessageDigest.getInstance("MD5")
        md5.update(info.toByteArray(charset("utf-8")))
        val encryption = md5.digest()
        val stringBuffer = StringBuffer()
        for (i in encryption.indices) {
            if (Integer.toHexString(0xff and encryption[i].toInt()).length == 1) {
                stringBuffer.append("0").append(
                    Integer.toHexString(
                        0xff and encryption[i]
                            .toInt()
                    )
                )
            } else {
                stringBuffer.append(Integer.toHexString(0xff and encryption[i].toInt()))
            }
        }
        stringBuffer.toString()
    } catch (e: Exception) {
        //            e.printStackTrace();
        ""
    }
}

//加密文件
fun md5ForFile(file: File?): String? {
    val bufferSize = 10240
    var fis: FileInputStream? = null
    val bytes = ByteArray(bufferSize)
    try {
        //创建MD5转换器和文件流
        val md5 = MessageDigest.getInstance("MD5")
        fis = FileInputStream(file)
        var length: Int
        while (fis.read(bytes).also { length = it } != -1) {
            md5.update(bytes, 0, length)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        fis?.close()
    }
    return byte2HexString(bytes)
}

private fun byte2HexString(source: ByteArray): String? {
    if (source.isEmpty()) {
        return null
    }
    val hexStr = StringBuilder(EMPTY)
    source.forEach {
        val value = it.toInt()
        val hex = Integer.toHexString(value and 0xff)
        if (hex.length == 1) {
            hexStr.append(0)
        }
        hexStr.append(hex)
    }
    return hexStr.substring(0, 16)
}