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

package com.techsenger.toolkit.http;

import com.techsenger.toolkit.http.request.Request;
import com.techsenger.toolkit.http.request.RequestEnvelope;
import com.techsenger.toolkit.http.response.ResponseEnvelope;

/**
 * Converts between JSON strings and request/response envelope objects. Used by the dispatcher to deserialize
 * incoming requests and serialize outgoing responses.
 *
 * @author Pavel Castornii
 */
public interface JsonConverter {

    /**
     * Deserializes the given JSON string into a {@link RequestEnvelope} containing a request of the specified type.
     *
     * @param <T>          the type of the request payload
     * @param str          the JSON string to deserialize, never {@code null}
     * @param requestClass the class of the request payload, never {@code null}
     * @return the deserialized request envelope, never {@code null}
     */
    <T extends Request> RequestEnvelope<T> fromJson(String str, Class<T> requestClass);

    /**
     * Serializes the given {@link ResponseEnvelope} into a JSON string.
     *
     * @param envelope the response envelope to serialize, never {@code null}
     * @return the JSON representation of the envelope, never {@code null}
     */
    String toJson(ResponseEnvelope<?> envelope);
}
