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

import com.techsenger.toolkit.http.session.SessionManager;

/**
 * Represents the HTTP server and provides access to its core components. Acts as the central entry point
 * for managing sessions and dispatching incoming requests.
 *
 * @author Pavel Castornii
 */
public interface Server {

    /**
     * Returns the session manager responsible for the lifecycle of user sessions.
     *
     * @return the session manager, never {@code null}
     * @see SessionManager
     */
    SessionManager getSessionManager();

    /**
     * Returns the dispatcher handler responsible for routing incoming requests to the appropriate endpoint handler.
     *
     * @return the dispatcher handler, never {@code null}
     * @see DispatcherHandler
     */
    DispatcherHandler getDispatcherHandler();
}
