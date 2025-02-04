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

package com.techsenger.toolkit.jpa;

/**
 *
 * @author Pavel Castornii
 */
public final class JpaParameters {

    public static final String JAKARTA_PERSISTENCE_PROVIDER = "jakarta.persistence.provider";

    public static final String JAKARTA_TRANSACTION_TYPE = "jakarta.persistence.transactionType";

    public static final String JAKARTA_JTA_DATASOURCE = "jakarta.persistence.jtaDataSource";

    public static final String JAKARTA_NON_JTA_DATASOURCE = "jakarta.persistence.nonJtaDataSource";

    public static final String JAKARTA_JDBC_DRIVER = "jakarta.persistence.jdbc.driver";

    public static final String JAKARTA_JDBC_URL = "jakarta.persistence.jdbc.url";

    public static final String JAKARTA_JDBC_USER = "jakarta.persistence.jdbc.user";

    public static final String JAKARTA_JDBC_PASSWORD = "jakarta.persistence.jdbc.password";

    public static final String JAKARTA_SHARED_CACHE_MODE = "jakarta.persistence.sharedCache.mode";

    public static final String JAKARTA_CACHE_RETRIEVE_MODE = "jakarta.persistence.cache.retrieveMode";

    public static final String JAKARTA_CACHE_STORE_MODE = "jakarta.persistence.cache.storeMode";

    public static final String JAKARTA_VALIDATION_MODE = "jakarta.persistence.validation.mode";

    public static final String JAKARTA_VALIDATION_FACTORY = "jakarta.persistence.validation.factory";

    public static final String JAKARTA_PERSIST_VALIDATION_GROUP = "jakarta.persistence.validation.group.pre-persist";

    public static final String JAKARTA_UPDATE_VALIDATION_GROUP = "jakarta.persistence.validation.group.pre-update";

    public static final String JAKARTA_REMOVE_VALIDATION_GROUP = "jakarta.persistence.validation.group.pre-remove";

    public static final String JAKARTA_LOCK_SCOPE = "jakarta.persistence.lock.scope";

    public static final String JAKARTA_LOCK_TIMEOUT = "jakarta.persistence.lock.timeout";

    public static final String JAKARTA_BEAN_MANAGER = "jakarta.persistence.bean.manager";

    private JpaParameters() {
        //empty
    }
}
