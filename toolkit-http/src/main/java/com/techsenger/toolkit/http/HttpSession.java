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

import com.techsenger.toolkit.http.security.SecurityContext;
import com.techsenger.toolkit.http.session.Session;
import java.time.Instant;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
class HttpSession implements Session {

    private static final Logger logger = LoggerFactory.getLogger(HttpSession.class);

    private String remoteHost;

    private int remotePort;

    private String uuid;

    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    /**
     * Unix time.
     */
    private long lastAccessedTime;

    private SecurityContext securityContext;

    HttpSession(String uuid) {
        setUuid(uuid);
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    @Override
    public boolean isClosed() {
        return this.closedAt != null;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    @Override
    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void touch() {
        this.setLastAccessedTime(Instant.now().getEpochSecond());
    }

    @Override
    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    public void close() throws Exception {
        setClosedAt(LocalDateTime.now());
        if (this.securityContext != null) {
            this.securityContext.close();
            this.securityContext = null;
        }
    }

    @Override
    public String toString() {
        return "Session{" + "uuid=" + getUuid() + ", lastAccessedTime=" + lastAccessedTime + '}';
    }
}
