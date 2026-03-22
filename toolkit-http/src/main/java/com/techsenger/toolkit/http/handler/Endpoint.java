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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as an HTTP endpoint handler and specifies the path it is responsible for.
 * Every {@link EndpointHandler} implementation must be annotated with this annotation.
 *
 * <p>By default, all endpoints are secured and require the client to be authenticated
 * and authorized. To make an endpoint publicly accessible, add the {@link Unsecured} annotation.
 *
 * <p>Example usage:
 * <pre>{@code
 * @Endpoint("/api/users")
 * public class GetUsersHandler implements EndpointHandler<MyContext, GetUsersRequest> { ... }
 * }</pre>
 *
 * @author Pavel Castornii
 * @see Unsecured
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Endpoint {

    /**
     * The endpoint path this handler is responsible for (e.g. {@code "/api/users"}).
     *
     * @return the endpoint path, never {@code null}
     */
    String value();
}
