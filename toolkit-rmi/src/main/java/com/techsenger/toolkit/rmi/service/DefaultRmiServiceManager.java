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

package com.techsenger.toolkit.rmi.service;

import com.techsenger.toolkit.rmi.exceptions.RmiServiceException;
import com.techsenger.toolkit.rmi.server.RmiRegistry;
import com.techsenger.toolkit.rmi.session.DefaultRmiSession;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class DefaultRmiServiceManager implements RmiServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRmiServiceManager.class);

    private final String serverInfo;

    private final RmiRegistry registry;

    /**
     * Service classes by interface. We use here string as interface name because different
     * classloaders return different classes and interfaces are not found.
     */
    private final Map<String, Class<? extends AbstractRmiService>> serviceClassesByInterface;

    private final Map<Class<? extends AbstractRmiStatelessService>, AbstractRmiStatelessService>
            statelessServicesByClass;

    private final Set<AbstractRmiStatefullService> statefullServices;

    /**
     * Constructor.
     * @param serverInfo - information about server.
     * @param registry - rmi registry.
     */
    public DefaultRmiServiceManager(final String serverInfo, final RmiRegistry registry) {
        this.serviceClassesByInterface = new ConcurrentHashMap<>();
        this.statelessServicesByClass = new ConcurrentHashMap<>();
        this.statefullServices = ConcurrentHashMap.newKeySet();
        this.registry = registry;
        this.serverInfo = serverInfo;
    }

    @Override
    public AbstractRmiService getService(final String interfaceName, final DefaultRmiSession session)
            throws RmiServiceException {
        boolean isStateless = false;
        Class<? extends AbstractRmiService> klass = serviceClassesByInterface.get(interfaceName);
        if (klass == null) {
            throw new RmiServiceException("Service of " + interfaceName + " is unknown on " + serverInfo);
        }
        if (AbstractRmiStatelessService.class.isAssignableFrom(klass)) {
            isStateless = true;
        }
        AbstractRmiService service;
        if (isStateless) {
            service = statelessServicesByClass.get(klass);
            if (service == null) {
                try {
                    service = createStatelessService((Class<? extends AbstractRmiStatelessService>) klass,
                            interfaceName);
                } catch (Exception ex) {
                    throw new RmiServiceException(ex);
                }
            }
        } else {
            try {
                service = createStatefullService((Class<? extends AbstractRmiStatefullService>) klass, interfaceName);
                AbstractRmiStatefullService statefullService = (AbstractRmiStatefullService) service;
                statefullService.setSession(session);
            } catch (Exception ex) {
                throw new RmiServiceException(ex);
            }
        }
        return service;
    }

    @Override
    public void registerService(final String interfaceName, final Class implementationClass) {
        if (serviceClassesByInterface.containsKey(interfaceName)) {
            throw new IllegalArgumentException("Service of " + interfaceName + " already exists");
        }
        serviceClassesByInterface.put(interfaceName, implementationClass);
        logger.debug("After registering service {} of {} there are {} registered services on {}",
                implementationClass, interfaceName, serviceClassesByInterface.size(), serverInfo);
    }

    @Override
    public void unregisterService(final String interfaceName) {
        if (!serviceClassesByInterface.containsKey(interfaceName)) {
            throw new IllegalArgumentException("Service of " + interfaceName + " wasn't registered");
        }
        serviceClassesByInterface.remove(interfaceName);
        logger.debug("After unregistering service of {} there are {} registered services on {}",
                interfaceName, serviceClassesByInterface.size(), serverInfo);
    }

    @Override
    public List<String> getAvailableServices() {
        List<String> list = new ArrayList();
        list.addAll(serviceClassesByInterface.keySet());
        return list;
    }

    @Override
    public void clearServiceInstances() {
        //now we unexporting statefull and stateless services
        for (Map.Entry<Class<? extends AbstractRmiStatelessService>, AbstractRmiStatelessService>
                entry:statelessServicesByClass.entrySet()) {
            destroyStatelessService(entry.getValue());
        }
        for (AbstractRmiStatefullService service:statefullServices) {
            destroyStatefullService(service);
        }
        statefullServices.clear();
        statelessServicesByClass.clear();
    }

    /**
     * Destroys statefull service object. This method protected because it is used in session.
     * @param service that will be destroyed.
     */
    public void destroyStatefullService(final AbstractRmiStatefullService service) {
        registry.unexport(service);
        statefullServices.remove(service);
        service.deinitialize();
        logger.debug("Statefull service instance of {} was destroyed. Now there are {} instances on {}",
                service.getInterfaceName(), statefullServices.size(), serverInfo);
    }

    /**
     * Creates stateless service.
     * @param klass of the service.
     * @param interfaceName of the service.
     * @return created service.
     * @throws Exception if there is an error.
     */
    private synchronized AbstractRmiStatelessService createStatelessService(
            final Class<? extends AbstractRmiStatelessService> klass, final String interfaceName)throws Exception {
        //double check singleton pattern is used
        //there can be only one instance of stateless service
        AbstractRmiStatelessService service = statelessServicesByClass.get(klass);
        if (service != null) {
            return service;
        }
        Constructor<?> constructor = klass.getConstructor(); //String.class
        //every service must have only one constructor - with socket and url.
        service = (AbstractRmiStatelessService) constructor.newInstance(); //new Object[] {}
        service.setServiceManager(this);
        service.setInterfaceName(interfaceName);
        statelessServicesByClass.put(klass, service);
        //null because service is private
        registry.export(service, null);
        service.initialize();
        logger.debug("Stateless service instance of {} was created. Now there are {} instances on {}",
                interfaceName, statelessServicesByClass.size(), serverInfo);
        return service;
    }

    /**
     * Destroys stateless service.
     * @param service that will be destroyed.
     */
    private synchronized void destroyStatelessService(final AbstractRmiStatelessService service) {
        registry.unexport(service);
        statelessServicesByClass.remove(service.getClass());
        service.deinitialize();
        logger.debug("Stateless service instance of {} was destroyed. Now there are {} instances on {}",
                service.getInterfaceName(), statelessServicesByClass.size(), serverInfo);
    }

    /**
     * Creates statefull service.
     * @param klass of the service.
     * @param interfaceName of the service.
     * @return created service.
     * @throws Exception if there is an error.
     */
    private AbstractRmiStatefullService createStatefullService(final Class<? extends AbstractRmiStatefullService> klass,
            final String interfaceName) throws Exception {
        Constructor<?> constructor = klass.getConstructor(); //String.class
        //every service must have only one constructor - with socket and url.
        AbstractRmiStatefullService service =
                (AbstractRmiStatefullService) constructor.newInstance(); //new Object[] {}
        service.setServiceManager(this);
        service.setInterfaceName(interfaceName);
        //null because it is private
        registry.export(service, null);
        statefullServices.add(service);
        service.initialize();
        logger.debug("Statefull service instance of {} was created. Now there are {} instances on {}",
                interfaceName, statefullServices.size(), serverInfo);
        return service;
    }

}
