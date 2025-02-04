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
import java.util.Objects;

/**
 *
 * @author Pavel Castornii
 */
public class ThreadModel implements Serializable {

    public static ThreadModel from(final Thread thread) {
        var model = new ThreadModel();
        model.id = thread.getId();
        model.state = thread.getState();
        model.name = thread.getName();
        model.priority = thread.getPriority();
        model.daemon = thread.isDaemon();
        model.group = thread.getThreadGroup().getName();
        model.alive = thread.isAlive();
        model.contextClassLoader = Objects.toString(thread.getContextClassLoader(), null);
        return model;
    }

    private long id;

    private String name;

    private String group;

    private int priority;

    private boolean daemon;

    private Thread.State state;

    private boolean alive;

    private String contextClassLoader;

    public ThreadModel() {

    }

    /**
     * Returns id.
     * @return id.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns state.
     * @return state.
     */
    public Thread.State getState() {
        return state;
    }

    /**
     * Returns name.
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns priority.
     * @return priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Returns true if thread is daemon or false if not.
     * @return true of false.
     */
    public boolean isDaemon() {
        return daemon;
    }

    /**
     * Returns group.
     * @return group.
     */
    public String getGroup() {
        return group;
    }

    /**
     * Returns true if thread id alive and false if not.
     * @return true or false.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Returns context class loader.
     * @return
     */
    public String getContextClassLoader() {
        return contextClassLoader;
    }

    @Override
    public String toString() {
        return "ThreadModel{" + "id=" + id + ", name=" + name + ", group=" + group
                + ", priority=" + priority + ", daemon=" + daemon + ", state=" + state
                + ", alive=" + alive + ", contextClassLoader" + contextClassLoader + '}';
    }
}
