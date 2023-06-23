package com.wynntils.antiope.util;

import java.io.File;
import java.io.IOException;

public final class FileUtils {
    public static File createTemporaryDirectory() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-discord-game-sdk-" + System.nanoTime());

        if (!(tempDir.exists() && tempDir.isDirectory()) && !tempDir.mkdir()) {
            throw new RuntimeException(new IOException("Cannot create temporary directory"));
        }

        tempDir.deleteOnExit();

        return tempDir;
    }
}
