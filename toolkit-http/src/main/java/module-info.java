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

module com.techsenger.toolkit.http {
    requires org.slf4j;
    requires com.techsenger.toolkit.core;
    requires jdk.httpserver;

    exports com.techsenger.toolkit.http;
    exports com.techsenger.toolkit.http.exceptions;
    exports com.techsenger.toolkit.http.handler;
    exports com.techsenger.toolkit.http.request;
    exports com.techsenger.toolkit.http.response;
    exports com.techsenger.toolkit.http.security;
    exports com.techsenger.toolkit.http.session;

    opens com.techsenger.toolkit.http.request;
    opens com.techsenger.toolkit.http.response;
}
