package com.andy.ik.util;

import java.io.Closeable;


public class IOUtil {

    public static void closeQuietly(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            closeQuietly(closeable);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
}
