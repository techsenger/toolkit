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

import com.techsenger.toolkit.rmi.server.AbstractRmiRemote;
import com.techsenger.toolkit.rmi.server.RmiRemoteAccess;

/**
 * All services must have zero argument constructor.
 * @author Pavel Castornii
 */
public abstract class AbstractRmiService extends AbstractRmiRemote {

    private RmiServiceManager serviceManager;

    private final RmiServiceType type;

    /**
     * Implemented interface. In String because of classloading problems.
     */
    private String interfaceName;

    /**
     * Every service must have this constructor with socket and url.
     * @param type of the service.
     */
    public AbstractRmiService(final RmiServiceType type) {
        //services are available only via sessions.
        super(RmiRemoteAccess.PRIVATE);
        this.type = type;
    }

    /**
     * Returns the type.
     * @return url.
     */
    public final RmiServiceType getType() {
        return type;
    }

    /**
     * This method is called after constructor. Default implementation is empty.
     */
    public void initialize() {
        //empty
    }

    /**
     * This method is called before removing service from server. Default implementation is empty.
     */
    public void deinitialize() {
        //empty
    }

    /**
     * Returns interface this services implements.
     * @return class of the interface.
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * Returns service manager.
     * @return service manager.
     */
    protected RmiServiceManager getServiceManager() {
        return serviceManager;
    }

    /**
     * Sets service manager.
     * @param serviceManager which controls this service.
     */
    void setServiceManager(final RmiServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Sets interface of the service.
     * @param value of the service.
     */
    void setInterfaceName(final String value) {
        this.interfaceName = value;
    }
}
