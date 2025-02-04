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
import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

/**
 *
 * @author Pavel Castornii
 */
public final class JpaUtils {

    private static class ResultWork<R> implements Work {

        private final SqlFunction<Connection, R> function;

        private R result;

        ResultWork(SqlFunction<Connection, R> function) {
            this.function = function;
        }

        @Override
        public void execute(Connection connection) throws SQLException {
            this.result = function.apply(connection);
        }

        public R getResult() {
            return result;
        }
    }

    /**
     * Determines if the given proxy or persistent collection is initialized.
     */
    public static boolean isProxyInitialized(Object proxy) {
        return Hibernate.isInitialized(proxy);
    }

    /**
     * This method allows to use Hibernate JDBC connection without adding dependency for hibernate.
     *
     * Note: connection must NOT be closed in function. It is Hibernate responsibility. At the same time don't
     * return from it JDBC elements, for example ResultSet etc.
     *
     * @param <R> the result of the function.
     * @param manager the entity manager.
     * @param function the function that takes as parameter Connection and returns R result.
     * @return
     */
    public static <R> R jdbcConnection(EntityManager manager, SqlFunction<Connection, R> function)
            throws Exception {
        Session session = manager.unwrap(Session.class);
        var resultWork = new ResultWork<R>(function);
        session.doWork(resultWork);
        return resultWork.getResult();
    }

    private JpaUtils() {
        //empty
    }
}
