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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class PropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    /**
     * Reads properties file in PropertiesUtils class module or another module. If properties file is in another module
     * then that module must open package with properties file to PropertiesUtils class module.
     *
     * As the resource might be in different module we can't use class.getResourceAsStream and everything
     * becomes more difficult.
     *
     * @param propertiesPackageClass the class that is in the same package as property file
     * @param propertiesFileName
     * @return
     * @throws IOException
     */
    public static Properties read(Class<?> propertiesPackageClass, String propertiesFileName) throws IOException {
        //firstly the package of the info class must be open to this module
        String packageName = propertiesPackageClass.getPackageName();
        Module thatModule = propertiesPackageClass.getModule();
        Module thisModule = PropertiesUtils.class.getModule();
        if (!thatModule.isOpen(packageName, thisModule)) {
            throw new IllegalStateException(StringUtils.format("Module {} didn't open package {} to module {}",
                    thatModule.getName(), packageName, thisModule.getName()));
        }
        String packagePath = packageName.replaceAll(Pattern.quote("."), "/");
        String propertiesPath = "/" + packagePath + "/" + propertiesFileName;
        logger.debug("Resolved properties path: {}", propertiesPath);
        //relative path
        try (InputStream is = propertiesPackageClass.getModule().getResourceAsStream(propertiesPath);
            BufferedInputStream bis = new BufferedInputStream(is)) {
            final Properties properties = new Properties();
            properties.load(bis);
            return properties;
        }
    }

    private PropertiesUtils() {
        //empty
    }

}
