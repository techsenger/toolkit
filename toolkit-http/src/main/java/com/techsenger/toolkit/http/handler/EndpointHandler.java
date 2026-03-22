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

package com.techsenger.toolkit.http.handler;

import com.techsenger.toolkit.http.request.Request;
import com.techsenger.toolkit.http.request.RequestContext;
import com.techsenger.toolkit.http.response.Response;

/**
 * Handles incoming requests for a specific endpoint. Each implementation is responsible for a single endpoint
 * and defines the request type it accepts, whether authentication is required, and the business logic to execute.
 *
 * @param <T> the type of request context available to this handler
 * @param <S> the type of request this handler accepts
 *
 * @author Pavel Castornii
 */
public interface EndpointHandler<T extends RequestContext, S extends Request> {

    /**
     * Returns the class of the request type this handler accepts. Used by the dispatcher to deserialize
     * the incoming JSON payload into the correct request object.
     *
     * @return the request class, never {@code null}
     */
    Class<S> getRequestClass();

    /**
     * Handles the incoming request and returns a response. This method is invoked by the dispatcher after
     * all security checks have passed.
     *
     * @param context the request context providing information about the remote client and session
     * @param request the deserialized request payload
     * @return the response to be serialized and sent back to the client, never {@code null}
     * @throws Exception if an error occurs during request processing
     */
    Response handle(T context, S request) throws Exception;
}
