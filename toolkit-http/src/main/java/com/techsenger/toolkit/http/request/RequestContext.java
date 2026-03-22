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

import com.techsenger.toolkit.http.Server;
import com.techsenger.toolkit.http.session.Session;

/**
 * Provides contextual information about an incoming request, including the identity of the remote client
 * and the session associated with the request, if any.
 *
 * @author Pavel Castornii
 */
public interface RequestContext {

    /**
     * Returns the server that is handling this request.
     *
     * @return the server, never {@code null}
     * @see Server
     */
    Server getServer();

    /**
     * Returns the hostname or IP address of the remote client that sent the request.
     *
     * @return the remote host, never {@code null}
     */
    String getRemoteHost();

    /**
     * Returns the port number of the remote client that sent the request.
     *
     * @return the remote port
     */
    int getRemotePort();

    /**
     * Returns the session associated with this request, or {@code null} if the client has not provided
     * a session UUID or the session no longer exists.
     *
     * @return the session, or {@code null} if no valid session is associated with this request
     * @see Session
     */
    Session getSession();
}
