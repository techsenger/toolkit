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

/**
 * Represents a security context for an authenticated user. The context is created by {@link SecurityContextFactory}
 * upon successful authentication and is associated with the user's session. It is used to check whether the user is
 * authorized to access a specific endpoint on each request. Must be closed when the user's session ends.
 *
 * @author Pavel Castornii
 */
public interface SecurityContext {

    /**
     * Returns the login name of the user.
     *
     * @return
     */
    String getLoginName();

    /**
     * Returns whether the authenticated user is authorized to access the given endpoint. This method is called on
     * each request to verify access rights.
     *
     * @param endpoint the endpoint to check access for
     * @return {@code true} if the user is authorized, {@code false} otherwise
     * @throws Exception if a technical error occurs during authorization check
     */
    boolean isAuthorized(String endpoint) throws Exception;

    /**
     * Closes this security context and releases any associated resources. This method is called when the user's
     * session ends, either by explicit logout or timeout.
     *
     * @throws Exception if an error occurs while closing
     */
    void close() throws Exception;
}
