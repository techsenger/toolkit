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

package com.techsenger.toolkit.rmi.server;

import java.net.InetSocketAddress;

/**
 * Configuration of the server.
 * @author Pavel Castornii
 */
public final class DefaultRmiServerConfig {

    /**
     * Configuration builder.
     */
    public final class ConfigBuilder {

        /**
         * Constructor.
         */
        private ConfigBuilder() {
        }

        /**
         * Sets the url of the server.
         * @param aUrl of the server.
         * @return config builder for further settings.
         */
        public ConfigBuilder setUrl(final String aUrl) {
            DefaultRmiServerConfig.this.url = aUrl;
            return this;
        }

        /**
         * Sets sockets address of the server.
         * @param aSocketAddress of the server.
         * @return config builder for further settings.
         */
        public ConfigBuilder setSocketAddress(final InetSocketAddress aSocketAddress) {
            DefaultRmiServerConfig.this.socketAddress = aSocketAddress;
            return this;
        }

        /**
         * Sets if ssl is enabled for this server.
         * @param aSsl true if connection with server must be secured or false for unsecured connection.
         * @return config builder for further settings.
         */
        public ConfigBuilder setSsl(final boolean aSsl) {
            DefaultRmiServerConfig.this.ssl = aSsl;
            return this;
        }

        /**
         * Sets if client also must present its ssl certification.
         * @param aSslClientAuthNeeded true if client must present its ssl certification or false if mustn't.
         * @return config builder for further settings.
         */
        public ConfigBuilder setSslClientAuthNeeded(final boolean aSslClientAuthNeeded) {
            DefaultRmiServerConfig.this.sslClientAuthNeeded = aSslClientAuthNeeded;
            return this;
        }

        /**
         * Sets session timeout.
         * @param value - time during which if session is not touched server will close it.
         * Default value is 0. Zero means that session has no timeout.
         * @return sessionTimeout.
         */
        public ConfigBuilder setSessionTimeout(final int value) {
            DefaultRmiServerConfig.this.sessionTimeout = value;
            return this;
        }

        /**
         * Builds configuration.
         * @return built configuration.
         */
        public DefaultRmiServerConfig build() {
            return DefaultRmiServerConfig.this;
        }
    }

    /**
     * Url of the server.
     */
    private String url;

    /**
     * Address of the server.
     */
    private InetSocketAddress socketAddress;

    /**
     * If connection with server is secured.
     */
    private boolean ssl;

    /**
     * If client must present its ssl certificate which will be checked by server.
     */
    private boolean sslClientAuthNeeded;

    /**
     * Session time-out in minutes. Zero value means, that there is no time out.
     * If session in not touched in time-out period then server will close this session.
     */
    private int sessionTimeout = 0;

    /**
     * Constructor.
     */
    private DefaultRmiServerConfig() {
        //does nothing.
    }

    /**
     * Creates new builder.
     * @return builder.
     */
    public static ConfigBuilder newBuilder() {
        return new DefaultRmiServerConfig().new ConfigBuilder();
    }

    /**
     * Returns the url of the server.
     * @return url of the server.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns address of the server.
     * @return address of the server.
     */
    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    /**
     * Shows if server must use secure connection.
     * @return true if must or false if mustn't.
     */
    public boolean isSsl() {
        return ssl;
    }

    /**
     * Shows if client must present ssl certificate.
     * @return true if must and false if mustn't.
     */
    public boolean isSslClientAuthNeeded() {
        return sslClientAuthNeeded;
    }

    /**
     * Returns session timeout. Time during which if session is not touched then it will be closed by server.
     * Default value is 0. Zero value means that session has no timeout.
     * @return session timeout.
     */
    public int getSessionTimeout() {
        return sessionTimeout;
    }
}
