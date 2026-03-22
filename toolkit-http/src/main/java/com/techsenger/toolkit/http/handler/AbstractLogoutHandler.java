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

import com.techsenger.toolkit.http.request.LogoutRequest;
import com.techsenger.toolkit.http.request.RequestContext;
import com.techsenger.toolkit.http.response.LogoutResponse;
import com.techsenger.toolkit.http.response.Response;

/**
 * Default logout handler.
 *
 * @author Pavel Castornii
 */
public abstract class AbstractLogoutHandler<T extends RequestContext>
        extends AbstractEndpointHandler<T, LogoutRequest> {

    public AbstractLogoutHandler() {
        super(LogoutRequest.class);
    }

    @Override
    public Response handle(T context, LogoutRequest request) throws Exception {
        var session = context.getSession();
        context.getServer().getSessionManager().closeSession(session.getUuid());
        return new LogoutResponse(true);
    }
}
