package com.example.solo_recipes.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun createTempFile(context: Context, prefix: String): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    return File.createTempFile("${prefix}_${timeStamp}_", ".jpg", context.cacheDir)
}

fun saveFileToInternal(context: Context, file: File): Uri {
    val imagesDir = File(context.filesDir, "images").apply { mkdirs() }
    val newFile = File(imagesDir, file.name)
    file.copyTo(newFile, overwrite = true)
    return Uri.fromFile(newFile)
}

fun saveImageToInternal(context: Context, uri: Uri): Uri {
    val imagesDir = File(context.filesDir, "images").apply { mkdirs() }
    val fileName = "IMG_${System.currentTimeMillis()}.jpg"
    val newFile = File(imagesDir, fileName)
    
    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(newFile).use { output ->
            input.copyTo(output)
        }
    }
    return Uri.fromFile(newFile)
}
