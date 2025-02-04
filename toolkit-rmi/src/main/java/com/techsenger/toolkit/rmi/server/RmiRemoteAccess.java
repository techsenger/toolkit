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

/**
 *
 * @author Pavel Castornii
 */
public enum RmiRemoteAccess {

    /**
     * Remote with this type of access will be available only to those, who get it via other remotes, for example,
     * via session.
     */
    PRIVATE,


    /**
     * Remote with this type of access will be added to Rmi Registry and will be available to all users connected
     * to certain RMI host:port.
     */
    PUBLIC
}
