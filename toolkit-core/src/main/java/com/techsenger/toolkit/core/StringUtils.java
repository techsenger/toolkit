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

import java.security.SecureRandom;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class which contain utilities for working with strings.
 * @author Pavel Castornii
 */
public final class StringUtils {

    /**
     * This string of chars which are used for generating.
     */
    private static final String CHAR_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * The decimal radix.
     */
    private static final int DECIMAL_RADIX = 10;

    /**
     * Hex characters.
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Generates string of certain length which has alphabet characters and digits.
     * @param length of the generated string.
     * @return generated string.
     */
    public static String generateAlphaDigitString(final int length) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_STRING.charAt(rnd.nextInt(CHAR_STRING.length())));
        }
        return sb.toString();
    }

    /**
     * Checks if some string is integer.
     * @param s string which will be checked.
     * @param radix on which the string will be checked.
     * @return true if string is integer, otherwise return false.
     */
    public static boolean isInteger(final String s, final int radix) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if some string is decimal integer. It is analog of isInteger(s,10).
     * @param s string which will be checked
     * @return true if string is decimal integer, otherwise return false.
     */
    public static boolean isDecimalInteger(final String s) {
        return isInteger(s, DECIMAL_RADIX);
    }

    /**
     * Converts array of bytes to hex string. It is used, for example, for passwords.
     * @param bytes
     * @return
     */
    public static String convertToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Capitalizes the first letter of the string.
     * @param string
     * @return
     */
    public static String capitalizeFirstLetter(String string) {
        if (string == null) {
            return null;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Lowercases the first letter of the string.
     * @param string
     * @return
     */
    public static String lowercaseFirstLetter(String string) {
        if (string == null) {
            return null;
        }
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    /**
     * Checks if inputString contains any from the substrings.
     * @param inputString
     * @param substrings
     * @return
     */
    public static boolean contains(String inputString, List<String> substrings) {
        return substrings.parallelStream().anyMatch(inputString::contains);
    }

    /**
     * Checks if string is a boolean value (true/false).
     * @param string
     * @return
     */
    public static boolean isBoolean(final String string) {
        if (string == null) {
            throw new IllegalArgumentException();
        }
        var lowered = string.toLowerCase();
        return "true".equals(lowered) || "false".equals(lowered);
    }

    /**
     * Left trim.
     * @param input
     * @return
     */
    public static String ltrim(String input) {
        if (input == null) {
            return null;
        }
        int i = 0;
        while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
            i++;
        }
        String output = input.substring(i);
        return output;
    }

    /**
     * Right trim.
     * @param input
     * @return
     */
    public static String rtrim(String input) {
        if (input == null) {
            return null;
        }
        int i = input.length() - 1;
        while (i >= 0 && Character.isWhitespace(input.charAt(i))) {
            i--;
        }
        String output = input.substring(0, i + 1);
        return output;
    }

    /**
     * Formats string replacing "{}" with String.valueOf(object) values.
     * @param str
     * @param arguments
     * @return
     */
    public static String format(String str, Object... arguments) {
        if (arguments.length == 0) {
            return str;
        }
        //split(delimiter) by default removes trailing empty strings from result array. To turn this mechanism off we
        //need to use overloaded version of split(delimiter, limit) with limit set to negative value
        var splits = str.split(Pattern.quote("{}"), -1);
        if (splits.length - 1 != arguments.length) {
            throw new IllegalArgumentException("Argument count mismatch");
        }
        StringBuilder builder = new StringBuilder();
        for (var i = 0; i < splits.length - 1; i++) {
            builder.append(splits[i]);
            builder.append(String.valueOf(arguments[i]));
        }
        //and the last split
        builder.append(splits[splits.length - 1]);
        return builder.toString();
    }

    /**
     * Counts how many times the substring appears in string.
     *
     * @return
     */
    public static int countMatches(String string, String substring) {
        int count = string.length() - string.replaceAll(Pattern.quote(substring), "").length();
        return count;
    }

    /**
     * Constructor.
     */
    private StringUtils() {
    }
}
