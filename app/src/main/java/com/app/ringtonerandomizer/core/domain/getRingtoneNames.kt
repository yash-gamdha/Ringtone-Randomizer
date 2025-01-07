package com.app.ringtonerandomizer.core.domain

import com.app.ringtonerandomizer.core.data.GlobalVariables
import java.io.File

fun getRingtoneNames(): List<String>? {
    val dir = File(GlobalVariables.PATH)
    if (dir.exists()) {
        if (dir.isDirectory) {
            val list = dir.listFiles()?.filter { it.isFile }?.map { it.name }
            return list
        }
    }
    dir.mkdirs()
    return emptyList()
}