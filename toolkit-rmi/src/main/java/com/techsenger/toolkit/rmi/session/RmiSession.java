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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * RmiSession interface.
 * @author Pavel Castornii
 */
public interface RmiSession extends Remote {

    /**
     * Returns the uuid. Calling this method updates session last access time. So it is good
     * not to call this method every time but saves uuid and use it on client
     * side (rmi perfomance)
     * @return uuid of the session.
     * @throws RemoteException if there is an RMI error.
     */
    UUID getUuid() throws RemoteException;

    /**
     * Returns service, It returns Remote because not real instance is sent to client but stub so we
     * can get ClassCastException.
     * Calling this method updates session last access time.
     * @param interfaceName of the service.
     * @return service.
     * @throws RemoteException if there is an RMI error.
     */
    Remote getService(String interfaceName) throws RemoteException;

    /**
     * Closes the session. Calling this method updates session last access time.
     * @throws RemoteException if there is an RMI error.
     */
    void close() throws RemoteException;

    /**
     * Shows if session is closed. Calling this method updates session last access time.
     * @return true if closed and false if open.
     * @throws RemoteException if there is an RMI error.
     */
    boolean isClosed() throws RemoteException;

    /**
     * Returns list of interfaces. This method can be used to check if some services (which are added
     * and removed dynamically are available). Calling this method updates session last access time.
     * @return list of interfaces. But names are string because this bundle sees only its classes.
     * @throws RemoteException if there is an RMI error.
     */
    List<String> getAvailableServices() throws RemoteException;

    /**
     * Updates last access time. This method is auto called when one of other session methods are called.
     * @throws RemoteException if there is an RMI error.
     */
    void touch() throws RemoteException;
}
