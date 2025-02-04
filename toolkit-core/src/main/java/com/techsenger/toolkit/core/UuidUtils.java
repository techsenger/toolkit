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

import java.util.Base64;
import java.util.UUID;

/**
 *
 * @author Pavel Castornii
 */
public final class UuidUtils {

    /**
     * Encodes UUID on base64 and makes it URL-safe.
     *
     * As "+" and "/" are not URL safe characters, so, they are replaced them with the following ones: "-" and "_"
     *
     * @param uuid
     * @return
     */
    public static String encode64(final UUID uuid) {
        //to remove last = we use ".withoutPadding"
        String uuidString = Base64.getEncoder().withoutPadding().encodeToString(toByteArray(uuid));
        //this code will generate uuids with A-Za-z0-9, +, /:
        //g85M3+pmS1qCgRPKCCy8Gg
        //hi8NFapwTR6Zfun5/J2Hzg
        uuidString = uuidString.replace('+', '-');
        uuidString = uuidString.replace('/', '_');
        return uuidString;
    }

    /**
     * Generates new random uuid encoded on base64.
     * @return
     */
    public static String generate() {
        var uuid = encode64(UUID.randomUUID());
        return uuid;
    }

    /**
     * Converts uuid to byte array.
     *
     * @param uuid
     * @return
     */
    private static byte[] toByteArray(final UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }
        return buffer;
    }

    private UuidUtils() {
        //empty
    }
}
