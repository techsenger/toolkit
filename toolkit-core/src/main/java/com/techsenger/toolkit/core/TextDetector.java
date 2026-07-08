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

/**
 * Utility methods for text/binary detection.
 *
 * <p>This class does not detect the exact character encoding. It only determines whether the provided byte data is
 * likely to represent text content.</p>
 *
 * <p>Supported encodings:</p>
 * <ul>
 *     <li>UTF-8 with and without BOM</li>
 *     <li>UTF-16 LE/BE with and without BOM</li>
 *     <li>UTF-32 LE/BE with and without BOM</li>
 *     <li>ASCII</li>
 *     <li>Single-byte encodings such as Windows-1251 and ISO-8859-x</li>
 * </ul>
 *
 * <p>The detection is heuristic. A binary file may theoretically contain byte sequences that look like text, and
 * unusual text encodings may not always be detected.</p>
 *
 * @author Pavel Castornii
 */
public final class TextDetector {

    /**
     * Checks whether the given bytes represent text content.
     *
     * <p>The method is intended for file manager use cases where it is necessary to decide whether a file can be
     * opened in a text editor.</p>
     *
     * <p>Usually it is enough to provide only the first few kilobytes of a file.</p>
     *
     * @param data bytes to analyze
     * @return {@code true} if the data is likely text, otherwise {@code false}
     */
    public static boolean isText(byte[] data) {
        if (data == null || data.length == 0) {
            return true;
        }

        // Unicode BOM is a very strong indication of text.
        if (hasUtfBom(data)) {
            return true;
        }

        // Detect UTF-16/UTF-32 without BOM.
        if (isLikelyUnicodeWithoutBom(data)) {
            return true;
        }

        int controlCharacters = 0;
        int printableCharacters = 0;

        for (byte value : data) {
            int b = value & 0xFF;

            // NULL byte is a strong binary indicator. UTF-16/UTF-32 were already handled above.
            if (b == 0) {
                return false;
            }

            // Common whitespace characters.
            if (b == '\t' || b == '\n' || b == '\r') {
                printableCharacters++;
                continue;
            }

            // Printable ASCII characters.
            if (b >= 32 && b < 127) {
                printableCharacters++;
            } else if (b < 32) {
                // Other control characters.
                controlCharacters++;
            }
        }

        // Valid UTF-8 is text.
        if (isValidUtf8(data)) {
            return printableCharacters > 0;
        }

        // Fallback for legacy single-byte encodings.
        if (printableCharacters == 0) {
            return false;
        }

        return controlCharacters < data.length / 20;
    }

    /**
     * Checks whether the byte array starts with a Unicode BOM.
     *
     * <p>Supported BOMs:</p>
     *
     * <ul>
     *     <li>UTF-8: EF BB BF</li>
     *     <li>UTF-32 LE: FF FE 00 00</li>
     *     <li>UTF-32 BE: 00 00 FE FF</li>
     *     <li>UTF-16 LE: FF FE</li>
     *     <li>UTF-16 BE: FE FF</li>
     * </ul>
     *
     * @param data bytes to check
     * @return {@code true} if BOM is detected
     */
    private static boolean hasUtfBom(byte[] data) {

        if (data.length >= 3 && (data[0] & 0xFF) == 0xEF && (data[1] & 0xFF) == 0xBB && (data[2] & 0xFF) == 0xBF) {
            return true;
        }

        // UTF-32 must be checked before UTF-16 because UTF-32 BOM starts with UTF-16 BOM bytes.
        if (data.length >= 4 && (data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xFE
                && (data[2] & 0xFF) == 0x00 && (data[3] & 0xFF) == 0x00) {
            return true;
        }

        if (data.length >= 4 && (data[0] & 0xFF) == 0x00 && (data[1] & 0xFF) == 0x00
                && (data[2] & 0xFF) == 0xFE && (data[3] & 0xFF) == 0xFF) {
            return true;
        }

        if (data.length >= 2 && (data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xFE) {
            return true;
        }

        if (data.length >= 2 && (data[0] & 0xFF) == 0xFE && (data[1] & 0xFF) == 0xFF) {
            return true;
        }

        return false;
    }

    /**
     * Detects UTF-16 and UTF-32 encodings without BOM.
     *
     * <p>The method analyzes zero byte distribution patterns. Unicode encodings using fixed-width characters produce
     * predictable zero byte positions for ASCII-range text.</p>
     *
     * @param data bytes to analyze
     * @return {@code true} if UTF-16 or UTF-32 pattern is detected
     */
    private static boolean isLikelyUnicodeWithoutBom(byte[] data) {
        if (data.length < 8) {
            return false;
        }
        int size = Math.min(data.length, 256);

        // UTF-32 LE: XX 00 00 00
        int utf32LeMatches = 0;
        int utf32LeBlocks = 0;
        for (int i = 0; i + 3 < size; i += 4) {
            utf32LeBlocks++;
            if ((data[i + 1] & 0xFF) == 0 && (data[i + 2] & 0xFF) == 0 && (data[i + 3] & 0xFF) == 0) {
                utf32LeMatches++;
            }
        }
        if (utf32LeBlocks >= 2 && utf32LeMatches >= utf32LeBlocks * 0.75) {
            return true;
        }

        // UTF-32 BE: 00 00 00 XX
        int utf32BeMatches = 0;
        int utf32BeBlocks = 0;
        for (int i = 0; i + 3 < size; i += 4) {
            utf32BeBlocks++;
            if ((data[i] & 0xFF) == 0 && (data[i + 1] & 0xFF) == 0 && (data[i + 2] & 0xFF) == 0) {
                utf32BeMatches++;
            }
        }
        if (utf32BeBlocks >= 2 && utf32BeMatches >= utf32BeBlocks * 0.75) {
            return true;
        }

        // UTF-16 LE: XX 00 XX 00
        int utf16LeMatches = 0;
        int utf16LeBlocks = 0;
        for (int i = 0; i + 1 < size; i += 2) {
            utf16LeBlocks++;
            if ((data[i] & 0xFF) != 0 && (data[i + 1] & 0xFF) == 0) {
                utf16LeMatches++;
            }
        }
        if (utf16LeBlocks >= 3 && utf16LeMatches >= utf16LeBlocks * 0.75) {
            return true;
        }

        // UTF-16 BE: 00 XX 00 XX
        int utf16BeMatches = 0;
        int utf16BeBlocks = 0;
        for (int i = 0; i + 1 < size; i += 2) {
            utf16BeBlocks++;
            if ((data[i] & 0xFF) == 0 && (data[i + 1] & 0xFF) != 0) {
                utf16BeMatches++;
            }
        }
        if (utf16BeBlocks >= 3 && utf16BeMatches >= utf16BeBlocks * 0.75) {
            return true;
        }
        return false;
    }

    /**
     * Validates UTF-8 byte sequences.
     *
     * @param data bytes to validate
     * @return {@code true} if bytes form valid UTF-8
     */
    private static boolean isValidUtf8(byte[] data) {
        int i = 0;
        while (i < data.length) {
            int b = data[i] & 0xFF;
            if (b <= 0x7F) {
                i++;
                continue;
            }
            int continuationBytes;

            if ((b & 0xE0) == 0xC0) {
                continuationBytes = 1;
            } else if ((b & 0xF0) == 0xE0) {
                continuationBytes = 2;
            } else if ((b & 0xF8) == 0xF0) {
                continuationBytes = 3;
            } else {
                return false;
            }

            if (i + continuationBytes >= data.length) {
                return false;
            }

            for (int j = 1; j <= continuationBytes; j++) {
                int next = data[i + j] & 0xFF;
                if ((next & 0xC0) != 0x80) {
                    return false;
                }
            }
            i += continuationBytes + 1;
        }
        return true;
    }


    private TextDetector() {
        // empty
    }
}
