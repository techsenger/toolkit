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

package com.techsenger.toolkit.core.model;

import java.io.Serializable;
import java.lang.management.ThreadInfo;

/**
 *
 * @author Pavel Castornii
 */
public class ThreadInfoModel implements Serializable {

    public static ThreadInfoModel from(ThreadInfo threadInfo) {
        var model = new ThreadInfoModel();
        model.threadId = threadInfo.getThreadId();
        model.threadName = threadInfo.getThreadName();
        model.threadState = threadInfo.getThreadState();
        model.lockName = threadInfo.getLockName();
        model.lockOwnerId = threadInfo.getLockOwnerId();
        model.lockOwnerName = threadInfo.getLockOwnerName();
        model.stackTraceElements = threadInfo.getStackTrace();
        return model;
    }

    private long threadId;

    private String threadName;

    private Thread.State threadState;

    private String lockName;

    private long lockOwnerId;

    private String lockOwnerName;

    private StackTraceElement[] stackTraceElements;

    public ThreadInfoModel() {

    }

    /**
     * Returns thread id.
     * @return thread id.
     */
    public long getThreadId() {
        return threadId;
    }

    /**
     * Returns thread name.
     * @return thread name.
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * Returns thread state.
     * @return thread state.
     */
    public Thread.State getThreadState() {
        return threadState;
    }

    /**
     * Returns lock name.
     * @return lock name.
     */
    public String getLockName() {
        return lockName;
    }

    /**
     * Returns id of the owner of the lock.
     * @return id of the owner of the lock.
     */
    public long getLockOwnerId() {
        return lockOwnerId;
    }

    /**
     * Returns name of the owner of the lock.
     * @return name of the owner of the lock.
     */
    public String getLockOwnerName() {
        return lockOwnerName;
    }

    /**
     * Returns stack trace elements.
     * @return stack trace elements.
     */
    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }
}
