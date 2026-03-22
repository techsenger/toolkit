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

package com.techsenger.toolkit.http.security;

import java.util.Optional;

/**
 * Factory for creating security contexts. This factory is responsible for authenticating users and creating their
 * security contexts. Implementations may use various authentication mechanisms such as databases, LDAP, or simple
 * credential files. Since implementations may hold resources such as database connections, they must be closed when
 * the server shuts down.
 *
 * <p>To provide a custom implementation, a module must declare it as a JPMS service:
 * <pre>
 *     provides SecurityContextFactory with MySecurityContextFactory;
 * </pre>
 *
 * @author Pavel Castornii
 */
public interface SecurityContextFactory {

    /**
     * Creates a security context for the given credentials. Returns an empty optional if authentication fails.
     * This method is called when a user attempts to connect to the server. Each created context is associated with a
     * single user session and must be closed when the session ends.
     *
     * @param loginName the login name of the user
     * @param loginPassword the login password of the user
     * @return an optional containing a new security context for the authenticated user, or an empty optional
     *         if authentication fails
     * @throws Exception if a technical error occurs during authentication
     */
    Optional<SecurityContext> create(String loginName, String loginPassword) throws Exception;

    /**
     * Closes this factory and releases any underlying resources such as database connections. This method is called
     * when the server shuts down.
     *
     * @throws Exception if an error occurs while closing
     */
    void close() throws Exception;
}
