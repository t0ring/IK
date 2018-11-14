package com.andy.ikt

import com.andy.ikt.util.IOUtil
import okio.*
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

fun main(args: Array<String>) {

    val source = "j"
    val target = ""
    val suffix = ""

    Thread { find(source, target, suffix) }.start()
    //Thread { copy("/home/andy/Projects", "rz.txt") }.start()
}

private fun find(source: String, target: String, suffix: String) {
    val dir = File(source)
    for (file in dir.listFiles()) {
        val path = file.absolutePath
        if (file.isDirectory && file.name !in arrayOf("build", ".idea", ".gradle")) {
            find(path, target, suffix)
            continue
        }
        if (path.endsWith(suffix)) {
            var source: BufferedSource? = null
            try {
                source = file.source().buffer()
                val content = source.readString(Charset.forName("utf-8"))
                if (content.contains(target, true)) {
                    System.out.println("cc contains in : $path")
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
    val target = File(target)
    if (target.exists()) {
        target.delete()
    }
    target.createNewFile()
    val out = target.sink().buffer()
    list(dir, out)
    System.out.println("cc Completed.")
}

private fun list(dir: File, out: BufferedSink) {
    for (file in dir.listFiles()) {
        if (file.isDirectory && file.name !in arrayOf("build", ".idea", ".gradle")) {
            list(file, out)
            continue
        }
        val path = file.absolutePath
        if (!path.endsWith(".kt")
                && !path.endsWith(".java")
                && !path.endsWith(".gradle")
                && !path.endsWith(".xml")) {
            continue
        }
        write(file, out)
    }
}

private fun write(file: File, out: BufferedSink) {
    var source: BufferedSource? = null
    try {
        source = file.source().buffer()
        out.writeAll(source)
        out.writeString("\n\n", Charset.forName("utf-8"))
        System.out.println("cc written file: ${file.absolutePath}")
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        IOUtil.closeQuietly(source)
    }
}