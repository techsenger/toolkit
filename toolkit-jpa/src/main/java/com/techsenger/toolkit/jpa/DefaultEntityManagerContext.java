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

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultEntityManagerContext implements EntityManagerContext {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerContext.class);

    /**
     * Entity manager for this context.
     */
    private final EntityManager entityManager;

    public DefaultEntityManagerContext() {
        this.entityManager = EntityManagerFactoryProvider.factory().createEntityManager();
        logger.debug("Created new context - {}", this);
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void close() {
        if (this.entityManager != null) {
            try {
                //Hibernate doesn't close connection if it isn't rolled back,so
                //if crud operation is cancelled we must roll back.
                var transaction = entityManager.getTransaction();
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                this.entityManager.close();
            } catch (Exception ex) {
                logger.error("Error closing EntityManager", ex);
            }
        }
        logger.debug("Closed context - {}", this);
    }

    @Override
    public String toString() {
        return "EntityManagerContext{" + "entityManager=" + entityManager + '}';
    }
}
