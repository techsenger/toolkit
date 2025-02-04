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

package com.techsenger.toolkit.rmi.session;

import com.techsenger.toolkit.rmi.server.RmiRegistry;
import com.techsenger.toolkit.rmi.service.DefaultRmiServiceManager;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.time.Instant;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class DefaultRmiSessionManager implements RmiSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRmiSessionManager.class);

    private final String serverInfo;

    private final RmiRegistry registry;

    /**
     * Sessions of the server.
     */
    private final Set<DefaultRmiSession> sessions;

    /**
     * Service manager. We need it to create services and for other tasks.
     */
    private final DefaultRmiServiceManager serviceManager;

    private final int sessionTimeout;

    private final class SessionScavenger {

        private Timer timer;

        /**
         * Constructor.
         */
        SessionScavenger() {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Iterator<DefaultRmiSession> iterator = sessions.iterator();
                    int collectedSessionCount = 0;
                    while (iterator.hasNext()) {
                        DefaultRmiSession session = iterator.next();
                        long currentTime = Instant.now().getEpochSecond();
                        //Checkstyle:OFF: MagicNumber
                        long sessionTimeoutSeconds = sessionTimeout * 60;
                        //Checkstyle:ON: MagicNumber
                        if (currentTime - session.getLastAccessedTime() >= sessionTimeoutSeconds) {
                            destroySession(session);
                            collectedSessionCount++;
                        }
                    }
                    logger.debug("SessionScavenger collected {} sessions on {}", collectedSessionCount, serverInfo);
                }
            };
            timer = new Timer(true);
            //Checkstyle:OFF: MagicNumber
            //we run scavenger every minute.
            timer.scheduleAtFixedRate(task, 0, 60 * 1000);
            //Checkstyle:ON: MagicNumber
        }

        /**
         * Terminates scanvenger.
         */
        public void terminate() {
            timer.cancel();  //Terminates this timer,discarding any currently scheduled tasks.
            timer.purge();   // Removes all cancelled tasks from this timer's task queue.
        }
    }

    /**
     * Session scavenger.
     */
    private SessionScavenger sessionScavenger;

    /**
     * Constructor.
     * @param serverInfo - rmi server info.
     * @param registry - rmi registry.
     * @param serviceManager - rmi service manager.
     * @param sessionTimeout - time out for session in minutes. If zero - there is no timeout.
     */
    public DefaultRmiSessionManager(final String serverInfo, final RmiRegistry registry,
            final DefaultRmiServiceManager serviceManager, final int sessionTimeout) {
        this.sessions = ConcurrentHashMap.newKeySet();
        this.serverInfo = serverInfo;
        this.registry = registry;
        this.serviceManager = serviceManager;
        this.sessionTimeout = sessionTimeout;
    }

    @Override
    public void destroyAllSessions() {
        //firstly we close sessions
        Iterator<DefaultRmiSession> it = sessions.iterator();
        while (it.hasNext()) {
           destroySession(it.next());
        }
        //to be quite safe
        sessions.clear();
    }

    @Override
    public DefaultRmiSession createSession() {
        try {
            DefaultRmiSession rmiSession = new DefaultRmiSession(serverInfo, this, getClientInetAddress(),
                    serviceManager);
            //context knows about session and session about context
            registry.export(rmiSession, null);
            sessions.add(rmiSession);
            logger.debug("A session was created for {}, now there are {} sessions on {}",
                    RemoteServer.getClientHost(), sessions.size(), serverInfo);
            return rmiSession;
        } catch (Exception ex) {
            logger.error("There was an error creating session on {}", serverInfo, ex);
        }
        return null;
    }

    /**
     * This method is called from session.close().
     * @param rmiSession - session to be closed.
     */
    @Override
    public synchronized void destroySession(final RmiSession rmiSession) {
        final DefaultRmiSession session = (DefaultRmiSession) rmiSession;
        session.terminateAllStatefullServices();
        registry.unexport(session);
        sessions.remove(session);
        logger.debug("A session was destroyed, now there are {} sessions on {}", sessions.size(),
                serverInfo);
    }

    /**
     * Simple server stage change handler.
     * @param serverIsOn - indicates if server is running or not.
     */
    public void doOnServerStateChange(final boolean serverIsOn) {
        if (sessionTimeout > 0) {
            if (serverIsOn) {
                sessionScavenger = this.new SessionScavenger();
            } else {
                sessionScavenger.terminate();
            }
        }
    }

    /**
     * Returns client inet address.
     * @return inet address.
     */
    private InetAddress getClientInetAddress() {
        InetAddress result = null;
        try {
            result = InetAddress.getByName(RemoteServer.getClientHost());
        } catch (UnknownHostException | ServerNotActiveException ex) {
            logger.error("There was a problem while converting string host to address on {}", serverInfo, ex);
        }
        return result;
    }
}
