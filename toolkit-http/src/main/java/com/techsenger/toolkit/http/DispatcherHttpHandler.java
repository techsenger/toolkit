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

package com.techsenger.toolkit.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.techsenger.toolkit.http.exceptions.AuthenticationException;
import com.techsenger.toolkit.http.exceptions.AuthorizationException;
import com.techsenger.toolkit.http.exceptions.ServerException;
import com.techsenger.toolkit.http.exceptions.VersionMismatchException;
import com.techsenger.toolkit.http.handler.Endpoint;
import com.techsenger.toolkit.http.handler.EndpointHandler;
import com.techsenger.toolkit.http.handler.Unsecured;
import com.techsenger.toolkit.http.request.Request;
import com.techsenger.toolkit.http.request.RequestContext;
import com.techsenger.toolkit.http.request.RequestContextFactory;
import com.techsenger.toolkit.http.request.RequestEnvelope;
import com.techsenger.toolkit.http.response.Response;
import com.techsenger.toolkit.http.response.ResponseEnvelope;
import com.techsenger.toolkit.http.security.SecurityContextFactory;
import com.techsenger.toolkit.http.session.SessionManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DispatcherHttpHandler implements DispatcherHandler, HttpHandler {

    private static final class EndpointHandlerDescriptor {

        private final String endpoint;

        private final boolean secured;

        private final Class<? extends EndpointHandler<?, ?>> type;

        private volatile EndpointHandler<?, ?> instance;

        EndpointHandlerDescriptor(String endpoint, boolean secured,
                Class<? extends EndpointHandler<?, ?>> type) {
            this.endpoint = endpoint;
            this.secured = secured;
            this.type = type;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public boolean isSecured() {
            return secured;
        }

        public Class<? extends EndpointHandler<?, ?>> getType() {
            return type;
        }

        public EndpointHandler<?, ?> getInstance() throws Exception {
            if (instance == null) {
                synchronized (this) {
                    if (instance == null) {
                        instance = type.getDeclaredConstructor().newInstance();
                    }
                }
            }
            return instance;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(DispatcherHttpHandler.class);

    private static EndpointHandlerDescriptor createDescriptor(Class<? extends EndpointHandler<?, ?>> clazz) {
        Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
        if (endpoint == null) {
            throw new IllegalArgumentException("Handler " + clazz.getName() + " is missing @Endpoint annotation");
        }
        boolean secured = clazz.getAnnotation(Unsecured.class) == null;
        return new EndpointHandlerDescriptor(endpoint.value(), secured, clazz);
    }

    private final SecurityContextFactory securityContextFactory;

    private final RequestContextFactory<?> requestContextFactory;

    private final JsonConverter jsonConverter;

    private final Map<String, EndpointHandlerDescriptor> descriptorsByEndpoint = new ConcurrentHashMap<>();

    private SessionManager sessionManager;

    public <T extends RequestContext> DispatcherHttpHandler(RequestContextFactory<T> rcf, JsonConverter jsonConverter,
            List<Class<? extends EndpointHandler<T, ?>>> handlerClasses, SecurityContextFactory scf) throws Exception {
        this.requestContextFactory = rcf;
        this.jsonConverter = jsonConverter;
        this.securityContextFactory = scf;
        for (var handlerClass : handlerClasses) {
            var descriptor = createDescriptor(handlerClass);
            var previous = descriptorsByEndpoint.put(descriptor.getEndpoint(), descriptor);
            if (previous != null) {
                throw new Exception("Found multiple handlers for endpoint " + descriptor.getEndpoint()
                        + "; one in " + previous.getType().getModule() + " and another in "
                        + descriptor.getType().getModule());
            }
        }
    }

    @Override
    public SecurityContextFactory getSecurityContextFactory() {
        return securityContextFactory;
    }

    @Override
    public Collection<String> getEndpoints() {
        return Collections.unmodifiableCollection(this.descriptorsByEndpoint.keySet());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String endpoint = null;
        try {
            endpoint = exchange.getRequestURI().getPath();
            var descriptor = descriptorsByEndpoint.get(endpoint);
            if (descriptor == null) {
                logger.info("Unknown endpoint: " + endpoint);
                send(new ResponseEnvelope<>(null, new ServerException("Unknown endpoint")), exchange);
                return;
            }
            var handler = descriptor.getInstance();
            String requestJson = this.readJson(exchange);
            var requestEnvelope = jsonConverter.fromJson(requestJson, handler.getRequestClass());
            HttpSession session = null;
            if (requestEnvelope.getSessionUuid() != null) {
                session = (HttpSession) sessionManager.getSession(requestEnvelope.getSessionUuid());
            }
            if (session != null) {
                session.touch();
            }
            var context = requestContextFactory.create(exchange, session);
            if (descriptor.isSecured()) {
                if (session == null || session.getSecurityContext() == null) {
                    throw new AuthenticationException();
                }
                if (!session.getSecurityContext().isAuthorized(endpoint)) {
                    throw new AuthorizationException();
                }
            }
            var response = invokeHandler(handler, context, requestEnvelope);
            send(new ResponseEnvelope<>(response, null), exchange);
        } catch (AuthenticationException | AuthorizationException | VersionMismatchException ex) {
            send(new ResponseEnvelope<>(null, ex), exchange);
        } catch (Exception ex) {
            logger.error("Error handling request to {}", endpoint, ex);
            send(new ResponseEnvelope<>(null, new ServerException(ex.getMessage())), exchange);
        }
    }

    void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @SuppressWarnings("unchecked")
    private Response invokeHandler(EndpointHandler<?, ?> handler, RequestContext context,
            RequestEnvelope<?> envelope) throws Exception {
        var typedHandler = (EndpointHandler<RequestContext, Request>) handler;
        var request = (Request) handler.getRequestClass().cast(envelope.getRequest());
        return typedHandler.handle(context, request);
    }

    private void send(ResponseEnvelope<?> response, HttpExchange exchange) {
        String responseJson = jsonConverter.toJson(response);
        this.writeJson(responseJson, exchange);
    }

    private String readJson(HttpExchange httpExchange) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            int len;
            while ((len = httpExchange.getRequestBody().read(buffer)) > 0) {
                    bos.write(buffer, 0, len);
            }
            String json = new String(bos.toByteArray(), StandardCharsets.UTF_8);
            logger.debug("JSON request: {}", json);
            return json;
        } catch (IOException ex) {
            logger.error("Error receiving data", ex);
            return null;
        }
    }

    private void writeJson(String json, HttpExchange httpExchange) {
        try {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            httpExchange.sendResponseHeaders(200, json.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(json.getBytes(StandardCharsets.UTF_8));
            os.close();
            logger.debug("JSON response: {}", json);
        } catch (IOException ex) {
            logger.error("Error sending data", ex);
        }
    }
}
