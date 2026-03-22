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

import com.techsenger.toolkit.http.security.SecurityContextFactory;
import java.util.Collection;

/**
 * Represents the central request dispatcher responsible for routing incoming requests to the appropriate
 * {@link EndpointHandler}. Provides access to the registered endpoints and the factory used to authenticate
 * incoming connections.
 *
 * @author Pavel Castornii
 */
public interface DispatcherHandler {

    /**
     * Returns an unmodifiable view of all endpoint paths currently registered with this dispatcher.
     *
     * @return an unmodifiable collection of endpoint paths, never {@code null}
     */
    Collection<String> getEndpoints();

    /**
     * Returns the factory used to authenticate users and create their security contexts upon login.
     *
     * @return the security context factory, never {@code null}
     * @see SecurityContextFactory
     */
    SecurityContextFactory getSecurityContextFactory();
}
