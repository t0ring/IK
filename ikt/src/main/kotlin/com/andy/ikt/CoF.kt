package com.andy.ikt

import com.andy.ikt.util.IOUtil
import okio.*
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

fun main() {

    val source = ""
    val target = "www"
    val suffix = ""

    Thread { find(source, target, suffix) }.start()
    //Thread { copy("/home/andy/Projects", "rz.txt") }.start()
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
                val content = source.readString(Charset.forName("utf-8"))
                if (content.contains(target, true)) {
                    println("cc contains in: $path")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                IOUtil.closeQuietly(source)
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
        IOUtil.closeQuietly(source)
    }
}