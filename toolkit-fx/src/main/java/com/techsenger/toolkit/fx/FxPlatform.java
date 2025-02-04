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

package com.techsenger.toolkit.fx;

import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class FxPlatform {

    private static final Logger logger = LoggerFactory.getLogger(FxPlatform.class);

    private static volatile boolean running;

    private FxPlatform() {
        //empty
    }

    /**
     * Can be called many times.
     */
    public static synchronized void start() {
        if (!running) {
            Platform.startup(() -> {
                Platform.setImplicitExit(false);
                //to catch exception in JavaFX thread
                Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                    logger.error("Error in FxApplication", e);
                });
            });
            running = true;
        }
    }

    /**
     * Can be called only once to stop platform and not to run it anymore.
     */
    public static synchronized void stop() {
        if (running) {
            //Javadoc says Platform.exit() may be called from any thread. However, if it is called from Runtime
            //shutdownHook on JavaFX thread it will cause thread lock in PlatformImp.runAndWait(Runnable, boolean)
            //because of this bug https://bugs.openjdk.org/browse/JDK-8320923
            //So, for now Platform.exit is called on JavaFX thread. In case of calling FxPlatform.stop from
            //shutdownHook Platform.exit() simply won't be called until issue is fixed. At the same time it at
            //least won't block thread.
            Platform.runLater(() -> Platform.exit());
            running = false;
        }
    }

    /**
    * Schedules the specified {@code Runnable} to be executed on the JavaFX Application thread
    * and blocks the current thread until the {@code Runnable} has finished execution.
    *
    * This method should not be called from the JavaFX Application thread itself,
    * as it will cause a deadlock by waiting on the same thread.
    *
    * @param runnable the {@code Runnable} to be executed on the JavaFX Application thread
    * @throws InterruptedException if the current thread is interrupted while waiting
    */
    public static void runLaterAndWait(Runnable runnable) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                runnable.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}
