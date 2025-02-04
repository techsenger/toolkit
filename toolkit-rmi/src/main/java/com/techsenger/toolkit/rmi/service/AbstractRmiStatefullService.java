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

import com.techsenger.toolkit.rmi.session.AbstractRmiSession;

/**
 * Base class of statefull service. For every RmiSession.getService is created and returned a new instance.
 *
 * @author Pavel Castornii
 */
public abstract class AbstractRmiStatefullService extends AbstractRmiService {

    private AbstractRmiSession session;

    /**
     * Constructor.
     */
    public AbstractRmiStatefullService() {
        super(RmiServiceType.STATEFULL);
    }

    /**
     * Stubs but not objects are sent to RMI client, so something like session.ungetSerive(..) doesn't work.
     * That's why we can only on object do methods to control their existence.
     * This method is available only on Statefull Services.
     */
    public void terminate() {
        session.terminateStatefullService(this);
    }

    /**
     * Sets session. This method we use in DefaultRmiServiceManager.
     * @param session to which this statefull service belongs.
     */
    void setSession(final AbstractRmiSession session) {
        this.session = session;
    }

    /**
     * Returns the session. This method we use in DefaultRmiServiceManager.
     * @return session.
     */
    AbstractRmiSession getSession() {
        return session;
    }
}
