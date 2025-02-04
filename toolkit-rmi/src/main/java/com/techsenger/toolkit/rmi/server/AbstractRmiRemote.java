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

import java.rmi.Remote;

/**
 * Class which all RmiRemotes must inherit.
 * @author Pavel Castornii
 */
public abstract class AbstractRmiRemote implements Remote {

    /**
     * Every Rmi Remote must have this field which is used for solving if remote must be exported to RMI registry.
     */
    private final RmiRemoteAccess access;

    /**
     * For performance solution context is saved.
     * @param access must passed to constructor.
     */
    public AbstractRmiRemote(final RmiRemoteAccess access) {
        this.access = access;
    }

    /**
     * Returns the access.
     * @return access.
     */
    public final RmiRemoteAccess getAccess() {
        return access;
    }
}
