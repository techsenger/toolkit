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
import com.techsenger.toolkit.rmi.session.DefaultRmiSession;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public interface RmiServiceManager {

    /**
     * Returns the service from server.
     * @param interfaceName of the service.
     * @param session from which service is requested.
     * @throws RmiServiceException - if service can't be created.
     * @return service of null if
     */
    AbstractRmiService getService(String interfaceName, DefaultRmiSession session) throws RmiServiceException;

    /**
     * Registers service on server.
     * @param interfaceName of the service.
     * @param implementationClass of the service.
     */
    void registerService(String interfaceName, Class implementationClass);

    /**
     * Unregisters service. New services of such class can not be created however the existing instances
     * are still available
     * @param interfaceName of the service.
     */
    void unregisterService(String interfaceName);

    /**
     * Returns available services of this server.
     * @return list of interfaces.  But names are string because this bundle sees only its classes.
     */
    List<String> getAvailableServices();

    /**
     * Clears all instances of services both statefull and stateless.
     */
    void clearServiceInstances();
}
