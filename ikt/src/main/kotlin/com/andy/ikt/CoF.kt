package com.andy.ikt

import okio.*
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

private val CHARSET_UTF8 = Charset.forName("utf-8")
private val CHARSET_GBK = Charset.forName("GBK")

fun main() {
    val dir = "E:\\Ads\\asr_arch"
    val target = ".CardView"
    val suffix = "xml"

    Thread { find(dir, target, suffix) }.start()
    //Thread { copy("dir", "rz.txt") }.start()
}

private fun find(dir: String, target: String, suffix: String) {
    val filter = { file: File -> file.name !in arrayOf("build", ".gradle", ".idea", ".git", ".svn") }
    File(dir).listFiles()?.filter(filter)?.forEach {
        val path = it.absolutePath
        if (it.isDirectory) {
            find(path, target, suffix)
            return@forEach
        }
        if (path.endsWith(suffix)) {
            var source: BufferedSource? = null
            try {
                source = it.source().buffer()
                val content = source.readString(CHARSET_GBK)
                if (content.contains(target, true)) {
                    println("cc contains in: $path")
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                source?.close()
            }
        }
    }
}

private fun copy(source: String, target: String) {
    val dir = File(source)
    val file = File(target)
    if (file.exists()) {
        file.delete()
    }
    file.createNewFile()
    val out = file.sink().buffer()
    list(dir, out)
    println("cc completed.")
}

private fun list(dir: File, out: BufferedSink) {
    val filter = { file: File -> file.name !in arrayOf("build", ".gradle", ".idea", ".git", ".svn") }
    dir.listFiles()?.filter(filter)?.forEach {
        if (it.isDirectory) {
            list(it, out)
            return@forEach
        }
        val path = it.absolutePath
        if (!path.endsWith(".kt")
                && !path.endsWith(".java")
                && !path.endsWith(".gradle")
                && !path.endsWith(".xml")
        ) {
            return@forEach
        }
        write(it, out)
    }
}

private fun write(file: File, out: BufferedSink) {
    var source: BufferedSource? = null
    try {
        source = file.source().buffer()
        out.writeAll(source)
        out.writeString("\n\n", Charset.forName("utf-8"))
        println("cc written file: ${file.absolutePath}")
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        source?.close()
    }
}