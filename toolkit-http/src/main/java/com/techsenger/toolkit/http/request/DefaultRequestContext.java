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
import com.techsenger.toolkit.http.Server;
import com.techsenger.toolkit.http.session.Session;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultRequestContext implements RequestContext {

    private final Server server;

    private final String remoteHost;

    private final int remotePort;

    private final Session session;

    public DefaultRequestContext(Server server, HttpExchange exchange, Session session) {
        this.server = server;
        remoteHost = exchange.getRemoteAddress().getHostString();
        remotePort = exchange.getRemoteAddress().getPort();
        this.session = session;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    @Override
    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public Session getSession() {
        return session;
    }
}
