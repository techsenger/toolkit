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
 * Marks an {@link EndpointHandler} as publicly accessible, bypassing authentication and authorization checks.
 *
 * <p>By default, all endpoints are secured. This annotation is the explicit opt-out — only add it when
 * the endpoint is intentionally public. Omitting it on a handler that should be public will result in
 * an authorization error, which is immediately visible during testing.
 *
 * <p>Example usage:
 * <pre>{@code
 * @Endpoint("/api/login")
 * @Unsecured
 * public class LoginHandler implements EndpointHandler<MyContext, LoginRequest> { ... }
 * }</pre>
 *
 * @author Pavel Castornii
 * @see Endpoint
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Unsecured {

}
