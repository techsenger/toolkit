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

package com.techsenger.toolkit.core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class StreamUtils {

    private static final Logger logger = LoggerFactory.getLogger(StreamUtils.class);

    /**
     * Reads stream and returns result as a string.
     * @param stream
     * @return
     */
    public static String readStream(InputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("Stream can't be null");
        }
        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        try (Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0;) {
                out.append(buffer, 0, numRead);
            }
            return out.toString();
        } catch (Exception ex) {
            logger.error("Error reading stream", ex);
            return null;
        }
    }

    private StreamUtils() {
        //empty
    }
}
