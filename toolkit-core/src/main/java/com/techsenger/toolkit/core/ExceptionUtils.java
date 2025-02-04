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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class ExceptionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionUtils.class);

    /**
     * Returns exception of the callable. Used in tests.
     * @param callable
     * @return
     */
    public static Throwable exceptionOf(Callable<?> callable) {
        try {
            callable.call();
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

    /**
     * Converts throwable to string with message and stack trace.
     * @param throwable
     * @return
     */
    public static String toString(Throwable throwable) {
        String result = null;
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            result = sw.toString();
        } catch (IOException ex) {
            logger.error("Error converting exception to String", ex);
        }
        return result;
    }

    private ExceptionUtils() {
        //empty
    }
}
