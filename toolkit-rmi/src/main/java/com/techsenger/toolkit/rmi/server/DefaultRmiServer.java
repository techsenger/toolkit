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

import com.techsenger.toolkit.rmi.service.DefaultRmiServiceManager;
import com.techsenger.toolkit.rmi.session.DefaultRmiSessionManager;
import com.techsenger.toolkit.rmi.session.RmiSession;
import java.rmi.RemoteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultRmiServer.
 *
 * <p>We can't use default rmi registry as it is open to all. Different solutions can use this class as it is linked
 * with socketaddress and there can't be situation when two or more solutions at the same time use the same
 * rmi socketaddress.</p>
 *
 * <p>In order this registry could build object itself they must have consturctor(IntetSocketAdddress) and before
 * using it the class for certain remote object must be set.</p>
 *
 * <p>The service always will be only in one instance for certain socketaddress and url. *</p>
 *
 * @author Pavel Castornii
 */
public final class DefaultRmiServer extends AbstractRmiRemote implements RmiServer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRmiServer.class);

    /**
     * Description of the server which is used in logs.
     */
    private final String info;

    private boolean isRunning = false;

    private final DefaultRmiServerConfig config;

    private final RmiRegistry registry;

    /**
     * AccessChecker which is used when there is a try to create session.
     */
    private final RmiAccessChecker accessChecker;

    private final DefaultRmiSessionManager sessionManager;

    private final DefaultRmiServiceManager serviceManager;

    /**
     * Constructor.
     * @param config of the server.
     * @param registry which will be used on this server.
     * @param accessChecker which will be used while creating new sessions.
     */
    public DefaultRmiServer(final DefaultRmiServerConfig config, final RmiRegistry registry,
            final RmiAccessChecker accessChecker) {
        super(RmiRemoteAccess.PUBLIC);
        this.accessChecker = accessChecker;
        if (accessChecker == null) {
            throw new IllegalArgumentException("Access checker can't be null.");
        }
        this.registry = registry;
        this.config = config;
        this.info = this.config.getSocketAddress() + " (" + this.config.getUrl() + ")";
        this.serviceManager = new DefaultRmiServiceManager(info, registry);
        //session has access to service manager in order to create/destroy services.
        this.sessionManager = new DefaultRmiSessionManager(info, registry, serviceManager, config.getSessionTimeout());
    }

    /**
     * Starts server.
     */
    public void start() {
        registry.export(this, this.config.getUrl());
        isRunning = true;
        sessionManager.doOnServerStateChange(isRunning);
        logger.debug("Server {} started", this);
    }

    /**
     * Stops server.
     */
    public void stop() {
        registry.unexport(this);
        isRunning = false;
        sessionManager.doOnServerStateChange(isRunning);
        sessionManager.destroyAllSessions();
        serviceManager.clearServiceInstances();
        logger.debug("Server {} stopped", this);
    }

    /**
     * Registers service on server.
     * @param interfaceName of the service - canonical name.
     * @param implementationClass of the service. We don't use .class because of classloading problems.
     */
    public void registerService(final String interfaceName, final Class implementationClass) {
        serviceManager.registerService(interfaceName, implementationClass);
    }

    /**
     * Unregisters service. New services of such class can not be created however the existing instances
     * are still available
     * @param interfaceName of the service - canonical name. We don't use .class because of classloading problems.
     */
    public void unregisterService(final String interfaceName) {
        serviceManager.unregisterService(interfaceName);
    }

    /**
     * Opens new session.
     * @param login name of the user
     * @param password of the user
     * @return session.
     * @throws RemoteException if there is rmi problem.
     */
    @Override
    public RmiSession openSession(final String login, final String password) throws RemoteException {
        if (accessChecker.check(login, password)) {
            return sessionManager.createSession();
        } else {
            return null;
        }
    }

    /**
     * Converts to string.
     * @return sting representation of the server.
     */
    @Override
    public String toString() {
        return "DefaultRmiServer{" + "socketAddress=" + config.getSocketAddress() + ", url=" + config.getUrl() + '}';
    }

    /**
     * Returns descriptions of the server.
     * @return short description of server which is used in logs.
     */
    public String getInfo() {
        return info;
    }

//    /**
//     * This method not only checks access but also updates session last access time.
//     * The using of this method guarantees that no one after reading traffic between client
//     * and server and after getting client uuid can make unauthenticated calls to service.
//     *
//     * Unfortunately RemoteServer has static method only to get client host but it doesn't
//     * give information about client port. I think this is due to fact that connections
//     * can be closed and open during work. However,if some clients are behind same NAT they
//     * will have same client host names. That's why there is only one way to get client session
//     * - to pass it as parameter for every request.
//     * @param sessionUuid
//     * @throws RmiAuthenticationException
//     * @deprecated This method is nonsense because RMI must check it itself for ip security.
//     */
//    protected boolean checkSession(UUID sessionUuid){
//        //0. we check if such session exists
//        DefaultRmiSession session=sessions.get(sessionUuid);
//        if (session==null){
//            return false;
//        }
//        //1. we check if session is still open
//        if (session.isClosed()){
//            return false;
//        }
//        //2. we check the ip current and the ip from which session was created
//        if (session.getClientAddress().equals(getClientInetAddress())){
//            session.updateLastAccessTime();
//            return true;
//        }else{
//            return false;
//        }
//    }
}








