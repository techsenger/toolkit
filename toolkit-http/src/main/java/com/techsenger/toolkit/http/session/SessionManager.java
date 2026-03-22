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
import java.util.Collection;

/**
 * Manages the lifecycle of user sessions, including creation, retrieval, and termination. Also provides the ability
 * to associate a security context with an authenticated session.
 *
 * @author Pavel Castornii
 */
public interface SessionManager {

    /**
     * Opens a new session for the given remote host and port. Any client can have a session, regardless of whether
     * they are authenticated.
     *
     * @param host the remote host of the client
     * @param port the remote port of the client
     * @return the newly created session
     */
    Session openSession(String host, int port);

    /**
     * Closes the session with the given UUID and releases any associated resources, including the security context
     * if one is present.
     *
     * @param uuid the UUID of the session to close
     */
    void closeSession(String uuid);

    /**
     * Closes the given session and releases any associated resources, including the security context if one is present.
     *
     * @param session the session to close
     */
    void closeSession(Session session);

    /**
     * Closes all active sessions and releases their associated resources. Typically called when the server is
     * shutting down.
     */
    void closeAllSessions();

    /**
     * Returns the session with the given UUID, or {@code null} if no such session exists.
     *
     * @param uuid the UUID of the session to look up
     * @return the session, or {@code null} if not found
     */
    Session getSession(String uuid);

    /**
     * Returns an unmodifiable view of all currently active sessions.
     *
     * @return an unmodifiable collection of active sessions
     */
    Collection<Session> getSessions();

    /**
     * Associates the given security context with the specified session. This method is called after successful
     * authentication to bind the user's identity and authorization rights to their session.
     *
     * @param session the session to update
     * @param ctx     the security context to associate with the session
     */
    void setSecurityContext(Session session, SecurityContext ctx);
}
