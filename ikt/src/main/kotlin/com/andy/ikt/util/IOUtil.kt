package com.andy.ikt.util

import java.io.Closeable


object IOUtil {

    fun closeQuietly(vararg closeables: Closeable) {
        closeables.map { closeQuietly(it) }
    }

    fun closeQuietly(closeable: Closeable?) {
        closeable?.apply {
            try {
                close()
            } catch (rethrown: RuntimeException) {
                throw rethrown
            } catch (ignored: Exception) {
            }
        }
    }
}
