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

import com.techsenger.toolkit.rmi.session.RmiSession;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Base interface of rmi server which is available on client side.
 * @author Pavel Castornii
 */
public interface RmiServer extends Remote {

    /**
     * Opens session if login and password are correct.
     * @param login of the user.
     * @param password of the user.
     * @return session if login and password are correct or null.
     * @throws RemoteException if there is an error.
     */
    RmiSession openSession(String login, String password) throws RemoteException;

}
