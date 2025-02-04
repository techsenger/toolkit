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

package com.techsenger.toolkit.rmi.exceptions;

/**
 * Basic exception of rmi service.
 *
 * @author Pavel Castornii
 */
public class RmiServiceException extends Exception {

    /**
     * Constructor.
     */
    public RmiServiceException() {
    }

    /**
     * Constructor.
     *
     * @param message for exception.
     */
    public RmiServiceException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message for exception.
     * @param cause of the exception.
     */
    public RmiServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param cause of the exception.
     */
    public RmiServiceException(final Throwable cause) {
        super(cause);
    }

}
