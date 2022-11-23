package com.devmeng.skinlib.skin.entity

import java.io.File

/**
 * Created by Richard -> MHS
 * Date : 2022/10/30  19:21
 * Version : 1
 */
data class Skin(val md5: String, val name: String, val skinUrl: String) {

    lateinit var file: File

    lateinit var path: String

    fun getSkinFile(file: File): File {
        this.file = File(file, name)
        path = this.file.absolutePath
        return this.file
    }

}