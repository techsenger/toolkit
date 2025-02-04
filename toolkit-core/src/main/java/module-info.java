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

module com.techsenger.toolkit.core {
    requires org.slf4j;
    requires java.xml;
    requires java.management;
    exports com.techsenger.toolkit.core;
    exports com.techsenger.toolkit.core.collection;
    exports com.techsenger.toolkit.core.file;
    exports com.techsenger.toolkit.core.function;
    exports com.techsenger.toolkit.core.jpms;
    exports com.techsenger.toolkit.core.model;
    exports com.techsenger.toolkit.core.os;
    exports com.techsenger.toolkit.core.project;
    exports com.techsenger.toolkit.core.ssl;
    exports com.techsenger.toolkit.core.version;
    exports com.techsenger.toolkit.core.xml;

    opens com.techsenger.toolkit.core.model;
    opens com.techsenger.toolkit.core.version;
}
