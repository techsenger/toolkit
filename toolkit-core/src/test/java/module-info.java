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

open module com.techsenger.toolkit.core {
    requires org.slf4j;
    requires java.xml;

    requires org.junit.jupiter.api;
    requires org.assertj.core;

    provides com.techsenger.toolkit.core.jpms.TestServiceA
            with com.techsenger.toolkit.core.jpms.TestServiceAProvider;

    provides com.techsenger.toolkit.core.jpms.TestServiceB
            with com.techsenger.toolkit.core.jpms.TestServiceBProvider1, com.techsenger.toolkit.core.jpms.TestServiceBProvider2;
}


