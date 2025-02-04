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

import com.techsenger.toolkit.core.os.OsUtils;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/**
 *
 * @author Pavel Castornii
 */
public final class JarUtils {

    /**
     * Gets the base location of the given class.
     * <p>
     * If the class is directly on the file system (e.g.,
     * "/path/to/my/package/MyClass.class") then it will return the base directory
     * (e.g., "file:/path/to").
     * </p>
     * <p>
     * If the class is within a JAR file (e.g.,
     * "/path/to/my-jar.jar!/my/package/MyClass.class") then it will return the
     * path to the JAR (e.g., "file:/path/to/my-jar.jar").
     * </p>
     * <p>
     * Use {@link JarUtils#urlToFile(URL)} to convert the result to a {@link File}.
     * </p>
     *
     * @param c The class whose location is desired.
     */
    public static URL getLocation(final Class<?> c) {
        if (c == null) {
            return null; // could not load the class
        }

        // try the easy way first
        try {
            final URL codeSourceLocation =
                c.getProtectionDomain().getCodeSource().getLocation();
            if (codeSourceLocation != null) {
                return codeSourceLocation;
            }
        } catch (final SecurityException e) {
            // NB: Cannot access protection domain.
        } catch (final NullPointerException e) {
            // NB: Protection domain or code source is null.
        }

        // NB: The easy way failed, so we try the hard way. We ask for the class
        // itself as a resource, then strip the class's path from the URL string,
        // leaving the base path.

        // get the class's raw resource path
        final URL classResource = c.getResource(c.getSimpleName() + ".class");
        if (classResource == null) {
            return null; // cannot find class resource
        }

        final String url = classResource.toString();
        final String suffix = c.getName().replace('.', '/') + ".class";
        if (!url.endsWith(suffix)) {
            return null; // weird URL
        }

        // strip the class's path from the URL string
        final String base = url.substring(0, url.length() - suffix.length());

        String path = base;

        // remove the "jar:" prefix and "!/" suffix, if present
        if (path.startsWith("jar:")) {
            path = path.substring(4, path.length() - 2);
        }

        try {
            return new URL(path);
        } catch (final MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts the given {@link URL} to its corresponding {@link File}.
     * <p>
     * This method is similar to calling {@code new File(url.toURI())} except that
     * it also handles "jar:file:" URLs, returning the path to the JAR file.
     * </p>
     *
     * @param url The URL to convert.
     * @return A file path suitable for use with e.g. {@link FileInputStream}
     * @throws IllegalArgumentException if the URL does not correspond to a file.
     */
    public static File urlToFile(final URL url) {
        return url == null ? null : urlToFile(url.toString());
    }

    /**
     * Converts the given {@link URL} to its corresponding {@link Path}.
     * @param url
     * @return
     */
    public static Path urlToPath(final URL url) {
        var file = urlToFile(url);
        if (file != null) {
            return file.toPath();
        } else {
            return null;
        }
    }

    /**
     * Converts the given URL string to its corresponding {@link File}.
     *
     * @param url The URL to convert.
     * @return A file path suitable for use with e.g. {@link FileInputStream}
     * @throws IllegalArgumentException if the URL does not correspond to a file.
     */
    public static File urlToFile(final String url) {
        String path = url;
        if (path.startsWith("jar:")) {
            // remove "jar:" prefix and "!/" suffix
            final int index = path.indexOf("!/");
            path = path.substring(4, index);
        }
        try {
            if (OsUtils.isWindows() && path.matches("file:[A-Za-z]:.*")) {
                path = "file:/" + path.substring(5);
            }
            return new File(new URL(path).toURI());
        } catch (final MalformedURLException e) {
            // NB: URL is not completely well-formed.
        } catch (final URISyntaxException e) {
            // NB: URL is not completely well-formed.
        }
        if (path.startsWith("file:")) {
            // pass through the URL as-is, minus "file:" prefix
            path = path.substring(5);
            return new File(path);
        }
        throw new IllegalArgumentException("Invalid URL: " + url);
    }

    private JarUtils() {
        //empty
    }
}
