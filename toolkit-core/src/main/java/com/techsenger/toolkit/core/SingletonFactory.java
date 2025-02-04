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

package com.techsenger.toolkit.core;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Warning: this is not per jpms module nor per jvm factory.
 *
 * @author Pavel Castornii
 */
public class SingletonFactory<T> {

    private static final Logger logger = LoggerFactory.getLogger(SingletonFactory.class);

    private final Supplier<T> supplier;

    private volatile T signleton;

    public SingletonFactory(Supplier<T> supplier) {
        this.supplier = supplier;
    }

//    /**
//     * Consider A -> B -> A -> B -> ... -> Stack Overflow (where -> is "uses")
//     *
//     * The problem is that we don't have instance A when we create B.
//     * So, if A and B are singletons we need two phases:
//     * 1) creating instance after which other can use reference to it.
//     * 2) binding services.
//     *
//     * Because firstly we create A, after we call A initializer that will create B...
//     *
//     * However, this case we have reference to service that may not be initialized.
//     *
//     * This is circular dependency that should be avoided.
//     *
//     * @param supplier
//     * @param initializer
//     */
//    public SingletonFactory(Supplier<T> supplier, Consumer<T> initializer) {
//        this.supplier = supplier;
//        this.initializer = initializer;
//    }

    public T singleton() {
        if (this.signleton == null) {
            createSingleton();
        }
        return this.signleton;
    }

    private synchronized void createSingleton() {
        try {
            if (this.signleton == null) {
                this.signleton = this.supplier.get();
                logger.debug("Created singleton of {}", this.signleton.getClass().getName());
            }
        } catch (Exception ex) {
            logger.error("Error creating sigleton", ex);
        }
    }
}
