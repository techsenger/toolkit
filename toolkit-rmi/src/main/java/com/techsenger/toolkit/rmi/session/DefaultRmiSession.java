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

import com.techsenger.toolkit.rmi.server.RmiRemoteAccess;
import com.techsenger.toolkit.rmi.service.AbstractRmiService;
import com.techsenger.toolkit.rmi.service.AbstractRmiStatefullService;
import com.techsenger.toolkit.rmi.service.DefaultRmiServiceManager;
import com.techsenger.toolkit.rmi.service.RmiServiceType;
import java.net.InetAddress;
import java.rmi.Remote;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class DefaultRmiSession extends AbstractRmiSession implements RmiSession {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRmiSession.class);

    /**
     * State of the session.
     */
    private boolean closed = false;

    private final UUID uuid;

    private final DefaultRmiSessionManager sessionManager;

    private final DefaultRmiServiceManager serviceManager;

    /**
     * The address of the user for who the session was opened.
     */
    private final InetAddress clientAddress;

    /**
     * Unix time.
     */
    private long lastAccessedTime;

    /**
     * Statefull services are all linked to certain session. So every session
     * has references to statefull services.
     */
    private final Set<AbstractRmiStatefullService> statefullServices;

    private final String serverInfo;

    /**
     * Constructor.
     * @param serverInfo information about server.
     * @param serviceManager service manager.
     * @param sessionManager session manager.
     * @param clientAdress address of the owner of the session.
     * @throws Exception if there is an error.
     */
    public DefaultRmiSession(final String serverInfo, final DefaultRmiSessionManager sessionManager,
            final InetAddress clientAdress, final DefaultRmiServiceManager serviceManager)
            throws Exception {
        super(RmiRemoteAccess.PRIVATE);
        this.serverInfo = serverInfo;
        if (clientAdress == null) {
            throw new Exception("Client adress can't be null on " + this.serverInfo);
        }
        this.uuid = UUID.randomUUID();
        this.statefullServices = ConcurrentHashMap.newKeySet();
        this.clientAddress = clientAdress;
        this.sessionManager = sessionManager;
        this.serviceManager = serviceManager;
        updateLastAccessTime();
    }

    /**
     * Closes the session. Be careful. This method calls sessionManager.destroySession.
     */
    @Override
    public void close() {
        if (closed) {
            throw new IllegalStateException("The session can't be closed more then once.");
        } else {
            closed = true;
            //now we can destroy session.
            sessionManager.destroySession(this);
        }
    }

    /**
     * Returns the service.
     * @param interfaceName of the service.
     * @return service.
     */
    @Override
    public Remote getService(final String interfaceName) {
        try {
            updateLastAccessTime();
            //server has references to both statefull and stateless services
            AbstractRmiService service = serviceManager.getService(interfaceName, this);
            if (service.getType() == RmiServiceType.STATEFULL) {
                AbstractRmiStatefullService statefullService = (AbstractRmiStatefullService) service;
                statefullServices.add(statefullService);
            }
            return service;
        } catch (Exception ex) {
            logger.error("There was an error getting service of {} on {}", interfaceName, serverInfo, ex);
        }
        return null;
    }

    /**
     * Returns the uuid.
     * @return uuid of the session.
     */
    @Override
    public UUID getUuid() {
        updateLastAccessTime();
        return uuid;
    }

    /**
     * Checks if session is closed.
     * @return true if session closed and false if open.
     */
    @Override
    public boolean isClosed() {
        updateLastAccessTime();
        return closed;
    }

    /**
     * Returns available services on the server.
     * @return list of service interfaces.
     */
    @Override
    public List<String> getAvailableServices() {
        updateLastAccessTime();
        return serviceManager.getAvailableServices();
    }

    @Override
    public void touch() {
        updateLastAccessTime();
    }

    /**
     * Terminates statefull service.
     * @param service that must be terminated.
     */
    @Override
    public void terminateStatefullService(final AbstractRmiStatefullService service) {
        statefullServices.remove(service);
        serviceManager.destroyStatefullService(service);
    }

    /**
     * Terminates all statefull services.
     */
    public void terminateAllStatefullServices() {
        //now we must terminate all statefull services that belong to this session
        Iterator<AbstractRmiStatefullService> iterator = statefullServices.iterator();
        while (iterator.hasNext()) {
            terminateStatefullService(iterator.next());
        }
    }

    /**
     * Returns last accessed time.
     * @return time.
     */
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    /**
     * Returns statefull services.
     * @return set of statefull services which are current working on this session.
     */
    protected Set<AbstractRmiStatefullService> getStatefullServices() {
        return statefullServices;
    }

    /**
     * Updates last access time of the session. This method is called in rmi session and rmi server.
     */
    private void updateLastAccessTime() {
        lastAccessedTime = Instant.now().getEpochSecond();
        logger.debug("Session of {} last access time was updated to {} on {}",
                clientAddress, lastAccessedTime, serverInfo);
    }

    /**
     * Returns client address.
     * @return inet address of the session owner.
     */
    protected InetAddress getClientAddress() {
        return clientAddress;
    }
}
