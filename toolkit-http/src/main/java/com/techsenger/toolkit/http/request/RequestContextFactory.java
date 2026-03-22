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

package com.techsenger.toolkit.http.request;

import com.sun.net.httpserver.HttpExchange;
import com.techsenger.toolkit.http.session.Session;

/**
 * Factory for creating {@link RequestContext} instances from an incoming HTTP exchange and its associated session.
 * Implementations may extend the base context with application-specific data extracted from the exchange.
 *
 * @param <T> the type of request context produced by this factory
 *
 * @author Pavel Castornii
 */
public interface RequestContextFactory<T extends RequestContext> {

    /**
     * Creates a new request context for the given HTTP exchange and session.
     *
     * @param exchange the HTTP exchange representing the incoming request, never {@code null}
     * @param session  the session associated with the request, or {@code null} if no valid session exists
     * @return a new request context, never {@code null}
     */
    T create(HttpExchange exchange, Session session);
}
