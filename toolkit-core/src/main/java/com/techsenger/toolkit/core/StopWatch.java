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

/**
 *
 * @author Pavel Castornii
 */
public final class StopWatch {

    private final long start = System.nanoTime();

    private long end;

    private long elapsed;

    private StopWatch() {
        //empty
    }

    /**
     * Creates stopwatch and starts it.
     * @return
     */
    public static StopWatch create() {
        return new StopWatch();
    }

    /**
     * Stops stopwatch.
     */
    public void stop() {
        this.end = System.nanoTime();
        this.elapsed = this.end - this.start;
    }

    /**
     * Returns elapsed time in nanoseconds.
     * @return
     */
    public long elapsedNanos() {
        return this.elapsed;
    }

    /**
     * Returns elapsed time in seconds with two places after point.
     * @return
     */
    public double elapsedSeconds() {
        double seconds = (double) elapsed / 1_000_000_000.0;
        return NumberUtils.round(seconds, 2);
    }

    /**
     * Returns start point in nanoseconds.
     * @return
     */
    public long getStart() {
        return start;
    }

    /**
     * Returns end point in nanoseconds.
     * @return
     */
    public long getEnd() {
        return end;
    }
}
