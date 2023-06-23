package com.wynntils.antiope.util;

import com.wynntils.antiope.core.DiscordGameSDKCore;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class FileUtils {
    public static File createTemporaryDirectory() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-discord-game-sdk-" + System.nanoTime());

        if (!(tempDir.exists() && tempDir.isDirectory()) && !tempDir.mkdir()) {
            throw new RuntimeException(new IOException("Cannot create temporary directory"));
        }

        tempDir.deleteOnExit();

        return tempDir;
    }

    public static File createTemporaryFileFromResource(String resourcePath) {
        InputStream inputStream = DiscordGameSDKCore.class.getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new RuntimeException("Could not find '" + resourcePath + "' in classpath");
        }

        String[] parts = resourcePath.split("/");
        String fileName = parts[parts.length - 1];

        File temporaryDirectory = FileUtils.createTemporaryDirectory();
        File temporaryFile = new File(temporaryDirectory, fileName);

        temporaryDirectory.deleteOnExit();
        temporaryFile.deleteOnExit();

        try {
            Files.copy(inputStream, temporaryFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return temporaryFile;
    }
}
