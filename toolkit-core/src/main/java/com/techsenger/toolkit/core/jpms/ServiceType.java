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

package com.techsenger.toolkit.core.jpms;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This class we use to create generic method with generic result type.
 * @author Pavel Castornii
 */
public class ServiceType<T> {

    private Class<T> serviceClass;

    /**
     * Constructor must be protected otherwise it can't get type!
     */
    protected ServiceType() {
        //Get "T" and assign it to this.entityClass
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = genericSuperclass.getActualTypeArguments()[0];
        if (type instanceof Class) {
            this.serviceClass = (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            this.serviceClass = (Class<T>) ((ParameterizedType) type).getRawType();
        }
    }

    public Class<T> getServiceClass() {
        return serviceClass;
    }
}
