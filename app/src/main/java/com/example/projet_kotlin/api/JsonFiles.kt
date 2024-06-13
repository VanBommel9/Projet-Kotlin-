package com.example.projet_kotlin.api

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

object JsonFiles {

    fun writeJsonToFile(context: Context, jsonString: String, fileName: String) {
        val file = File(context.getExternalFilesDir(null), fileName)
        try {
            val outputStream: OutputStream = FileOutputStream(file)
            outputStream.write(jsonString.toByteArray(Charset.defaultCharset()))
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readJsonFromFile(context: Context, fileName: String): String? {
        val file = File(context.getExternalFilesDir(null), fileName)
        return try {
            val inputStream: InputStream = file.inputStream()
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}