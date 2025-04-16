/*
 * Copyright 2016-2025 Pavel Castornii.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techsenger.toolkit.core.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author Pavel Castornii
 */
public final class FileUtils {

    /**
     * Reads file to string. Example: String content = readFile("test.txt", StandardCharsets.UTF_8);
     * @param path of the file.
     * @param encoding of the file.
     * @return string which is the data of file.
     * @throws IOException exception.
     */
    public static String readFile(final Path path, final Charset encoding)throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

    /**
     * Writes text to file.
     * @param path of the file
     * @param text that will be written.
     * @param encoding of the file.
     */
    public static void writeFile(final Path path, final String text, final Charset encoding) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
            OutputStreamWriter streamWriter = new OutputStreamWriter(fileOutputStream, encoding);
            Writer writer = new BufferedWriter(streamWriter)) {
            writer.write(text);
            writer.flush();
        }
    }

    /**
     * Returns file name without extension.
     *
     * @param fileName
     * @return
     */
    public static String removeExtension(String fileName) {
        return fileName.replaceFirst("[.][^.]+$", "");
    }

    /**
     * Returns extension of the file.
     *
     * @param fileName
     * @return
     */
    public static String getExtension(String fileName) {
        String extension = null;
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }


    /**
     * Recursively deletes directory with all contents.
     * @param file
     */
    public static boolean deleteDirectory(File file) throws FilePermissionException {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    if (!deleteDirectory(f)) {
                        return false;
                    }
                }
            }
        }
        try {
            return file.delete();
        } catch (SecurityException e) {
            throw new FilePermissionException(file);
        }
    }

    /**
     * Formats file size to human readable format.
     * @param bytes
     * @return
     */
    public static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int z = (63 - Long.numberOfLeadingZeros(bytes)) / 10;
        var result = String.format(Locale.US, "%.1f %sB", (double) bytes / (1L << (z * 10)), " KMGTPE".charAt(z));
        return result;
    }

    /**
     * Converts POSIX file permissions to Linux string.
     *
     * @param permissions
     * @param separated the flag shows if between user and group, group and other permissions space if added.
     * @return
     */
    public static String toString(Set<PosixFilePermission> permissions, boolean separated) {
        if (permissions == null) {
            return null;
        }
        // Convert permissions to a string representation
        StringBuilder builder = new StringBuilder();

        // Owner permissions
        builder.append(permissions.contains(PosixFilePermission.OWNER_READ) ? 'r' : '-');
        builder.append(permissions.contains(PosixFilePermission.OWNER_WRITE) ? 'w' : '-');
        builder.append(permissions.contains(PosixFilePermission.OWNER_EXECUTE) ? 'x' : '-');
        if (separated) {
            builder.append(' ');
        }
        // Group permissions
        builder.append(permissions.contains(PosixFilePermission.GROUP_READ) ? 'r' : '-');
        builder.append(permissions.contains(PosixFilePermission.GROUP_WRITE) ? 'w' : '-');
        builder.append(permissions.contains(PosixFilePermission.GROUP_EXECUTE) ? 'x' : '-');
        if (separated) {
            builder.append(' ');
        }
        // Others permissions
        builder.append(permissions.contains(PosixFilePermission.OTHERS_READ) ? 'r' : '-');
        builder.append(permissions.contains(PosixFilePermission.OTHERS_WRITE) ? 'w' : '-');
        builder.append(permissions.contains(PosixFilePermission.OTHERS_EXECUTE) ? 'x' : '-');
        return builder.toString();
    }

    /**
     * Constructor.
     */
    private FileUtils() {
        //empty
    }
}
