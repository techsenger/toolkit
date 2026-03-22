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

package com.techsenger.toolkit.http.session;

import com.techsenger.toolkit.http.security.SecurityContext;
import java.time.LocalDateTime;

/**
 * Represents a user session established between a remote client and the server. A session is created when a client
 * connects and remains active until it is explicitly closed or expires due to inactivity.
 *
 * <p>A session may or may not be authenticated. Authentication is reflected by the presence of a
 * {@link SecurityContext}, which is associated with the session upon successful login and cleared when the session
 * ends.
 *
 * @author Pavel Castornii
 */
public interface Session {

    /**
     * Returns the unique identifier of this session.
     *
     * @return the session UUID, never {@code null}
     */
    String getUuid();

    /**
     * Returns the hostname or IP address of the remote client.
     *
     * @return the remote host, never {@code null}
     */
    String getRemoteHost();

    /**
     * Returns the port number of the remote client.
     *
     * @return the remote port
     */
    int getRemotePort();

    /**
     * Returns the current status of this session.
     *
     * @return the session status, never {@code null}
     * @see SessionStatus
     */
    SessionStatus getStatus();

    /**
     * Returns the date and time when this session was opened.
     *
     * @return the time the session was opened, never {@code null}
     */
    LocalDateTime getOpenedAt();

    /**
     * Returns the date and time when this session was closed, or {@code null} if the session is still active.
     *
     * @return the time the session was closed, or {@code null} if not yet closed
     */
    LocalDateTime getClosedAt();

    /**
     * Returns the security context associated with this session, or {@code null} if the client has not yet
     * authenticated. The security context is set upon successful login and cleared when the session ends.
     *
     * @return the security context, or {@code null} if the session is not authenticated
     * @see SecurityContext
     */
    SecurityContext getSecurityContext();
}
