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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public final class JpmsUtils {

    public static void printEnvironment(boolean showStandard) {
        String[] splits = null;
        List<String> list = new ArrayList<>();

        if (System.getProperty("jdk.module.path") != null) {
            splits = System.getProperty("jdk.module.path").split(":");
            System.out.println(System.getProperty("line.separator") + "MODULE PATH:");
            for (String split : splits) {
                list.add(split);
            }
        }
        sortAndPrint(list);

        System.out.println(System.getProperty("line.separator") + "CLASS PATH:");
        splits = System.getProperty("java.class.path").split(":");
        for (String split : splits) {
            list.add(split);
        }
        sortAndPrint(list);

        System.out.println(System.getProperty("line.separator") + "BOOT LAYER:");
        if (showStandard) {
            ModuleLayer.boot().modules().stream().forEach(m -> {
                list.add(m.getName());
            });
        } else {
            ModuleLayer.boot().modules().stream().forEach(m -> {
                if (!ModuleUtils.isStandard(m.getName())) {
                    list.add(m.getName());
                }
            });
        }
        sortAndPrint(list);
        System.out.println("");
    }

    private static void sortAndPrint(List<String> list) {
        Collections.sort(list);
        for (String s : list) {
            System.out.println(s);
        }
        list.clear();
    }

    private JpmsUtils() {
        //empty
    }
}
