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
import com.techsenger.toolkit.http.session.SessionManager;
import com.techsenger.toolkit.http.session.SessionStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
class DefaultSessionManager implements SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSessionManager.class);

    private final class SessionCleanupTask extends TimerTask {

        private static final long SESSION_TIMEOUT_SECONDS = 30 * 60;

        @Override
        public void run() {
            int collectedSessionCount = 0;
            var iterator = sessionsByUuid.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                HttpSession session = entry.getValue();
                long currentTime = Instant.now().getEpochSecond();
                // delete if value is up to date, otherwise leave for next round
                if (currentTime - session.getLastAccessedTime() >= SESSION_TIMEOUT_SECONDS) {
                    iterator.remove();
                    closeSession(session);
                    collectedSessionCount++;
                }
            }
            logger.debug("CleanupTask collected {} sessions; currently {} sessions", collectedSessionCount,
                    sessionsByUuid.size());
        }
    };

    private final Map<String, HttpSession> sessionsByUuid = new ConcurrentHashMap<>();

    private Timer cleanupTimer;

    private TimerTask cleanupTask;

    @Override
    public Session openSession(String remoteHost, int remotePort) {
        //we DO NOT encode in 64 base, because Shiro uses ordinary format.
        String uuid = UUID.randomUUID().toString();
        HttpSession session = new HttpSession(uuid);
        session.setRemoteHost(remoteHost);
        session.setRemotePort(remotePort);
        session.setStatus(SessionStatus.OPEN);
        var now = LocalDateTime.now();
        session.setOpenedAt(now);
        long epochSeconds = now.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        session.setLastAccessedTime(epochSeconds);
        this.sessionsByUuid.put(uuid, session);
        logger.debug("Session with uuid {} was opened; currently {} sessions", uuid, this.sessionsByUuid.size());
        return session;
    }

    @Override
    public void closeSession(String uuid) {
        HttpSession session = this.sessionsByUuid.remove(uuid);
        closeSession(session);
    }

    @Override
    public void closeSession(Session session) {
        if (session != null) {
            try {
                ((HttpSession) session).close();
            } catch (Exception ex) {
                logger.error("Error closing session", ex);
            }
            logger.debug("Session with uuid {} was closed; currently {} sessions", session.getUuid(),
                    this.sessionsByUuid.size());
        }
    }

    @Override
    public void closeAllSessions() {
        this.sessionsByUuid.values().forEach(s -> closeSession(s));
    }

    @Override
    public HttpSession getSession(String uuid) {
        HttpSession session = this.sessionsByUuid.get(uuid);
        return session;
    }

    @Override
    public Collection<Session> getSessions() {
        return Collections.unmodifiableCollection(sessionsByUuid.values());
    }

    @Override
    public void setSecurityContext(Session session, SecurityContext securityContext) {
        HttpSession httpSession = (HttpSession) session;
        httpSession.setSecurityContext(securityContext);
    }

    void startCleanup() {
        cleanupTask = this.new SessionCleanupTask();
        cleanupTimer = new Timer(true);
        //every five minutes.
        var time = 5 * 60 * 1000;
        cleanupTimer.scheduleAtFixedRate(this.cleanupTask, time, time);
        logger.debug("Cleanup task was created");
    }

    void stopCleanup() {
        cleanupTask.cancel();
        cleanupTimer.cancel();
        cleanupTask = null;
        cleanupTimer = null;
        logger.debug("Cleanup task was destroyed");
    }
}
