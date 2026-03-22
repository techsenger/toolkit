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

package com.techsenger.toolkit.http.handler;

import com.techsenger.toolkit.core.version.Version;
import com.techsenger.toolkit.http.exceptions.AuthenticationException;
import com.techsenger.toolkit.http.exceptions.VersionMismatchException;
import com.techsenger.toolkit.http.request.LoginRequest;
import com.techsenger.toolkit.http.request.RequestContext;
import com.techsenger.toolkit.http.response.LoginResponse;
import com.techsenger.toolkit.http.response.Response;

/**
 * Default login handler.
 *
 * @author Pavel Castornii
 */
public abstract class AbstractLoginHandler<T extends RequestContext> extends AbstractEndpointHandler<T, LoginRequest> {

    public AbstractLoginHandler() {
        super(LoginRequest.class);
    }

    @Override
    public Response handle(T context, LoginRequest request) throws Exception {
        if (request == null) {
            throw new AuthenticationException();
        }
        var server = context.getServer();
        var version = getVersion(context);
        var contextFactory = server.getDispatcherHandler().getSecurityContextFactory();
        if (version != null && !version.equals(request.getVersion())) {
            throw new VersionMismatchException();
        }
        var securityContext = contextFactory.create(request.getLoginName(), request.getLoginPassword());
        if (securityContext.isEmpty()) {
            throw new AuthenticationException();
        }
        var session = context.getSession();
        if (session == null) {
            session = server.getSessionManager().openSession(context.getRemoteHost(), context.getRemotePort());
        }
        server.getSessionManager().setSecurityContext(session, securityContext.get());
        return new LoginResponse(session.getUuid());
    }

    protected abstract Version getVersion(T context);
}
