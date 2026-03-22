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

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import com.techsenger.toolkit.core.ssl.SslUtils;
import com.techsenger.toolkit.http.session.SessionManager;
import java.net.InetSocketAddress;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple HTTP server designed for localhost only. Supports authentication, authorization and SSL.
 *
 * @author Pavel Castornii
 */
public class SimpleHttpServer implements Server {

    private static final Logger logger = LoggerFactory.getLogger(SimpleHttpServer.class);

    private final DefaultSessionManager sessionManager = new DefaultSessionManager();

    private final DispatcherHttpHandler dispatcherHandler;

    private volatile HttpServer server;

    public SimpleHttpServer(DispatcherHttpHandler dispatcherHandler) throws Exception {
        this.dispatcherHandler = dispatcherHandler;
        this.dispatcherHandler.setSessionManager(sessionManager);
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public DispatcherHandler getDispatcherHandler() {
        return this.dispatcherHandler;
    }

    /**
     * Starts server.
     *
     * @param host
     * @param port
     * @param sslCertificateAlias the alias or null if SSL is not used.
     */
    public void start(String host, int port, String sslCertificateAlias) {
        if (this.server != null) {
            throw new IllegalStateException("Server is already running");
        }
        try {
            var address = new InetSocketAddress(host, port);
            if (sslCertificateAlias != null) {
                this.server = HttpsServer.create(address, 0);
                // Set up the HTTPS context and parameters
                SSLContext sslContext = SslUtils.buildContext(sslCertificateAlias);
                var httpsConfigurator = new DefaultHttpConfigurator(sslContext);
                ((HttpsServer) this.server).setHttpsConfigurator(httpsConfigurator);
            } else {
                this.server = HttpServer.create(address, 0);
            }
            server.createContext("/", dispatcherHandler);
            server.setExecutor(null); // creates a default executor
            server.start();
            sessionManager.startCleanup();
            logger.info("HTTP server started on {}", address);
        } catch (Exception ex) {
            logger.error("Error starting HTTP server", ex);
        }
    }

    public void stop() {
        if (this.server == null) {
            throw new IllegalStateException("Server is not running");
        } else {
            this.server.stop(0);
            var address = this.server.getAddress();
            this.server = null;
            sessionManager.stopCleanup();
            sessionManager.closeAllSessions();
            logger.info("HTTP server stopped on {}", address);
        }
    }

    public HttpServer unwrap() {
        return this.server;
    }
}
