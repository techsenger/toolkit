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

package com.techsenger.toolkit.core.project;

import com.techsenger.toolkit.core.PropertiesUtils;
import com.techsenger.toolkit.core.version.Version;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class ProjectProperties {

    private static final Logger logger = LoggerFactory.getLogger(ProjectProperties.class);

    /**
     * Creates project info on the base of properties file content.
     *
     * @param propertiesPackageClass the class that is in the same package as property file
     * @param propertiesFileName
     * @throws IOException
     */
    public static ProjectProperties readFrom(Class<?> propertiesPackageClass, String propertiesFileName)
            throws IOException {
        var properties = PropertiesUtils.read(propertiesPackageClass, propertiesFileName);
        var info = new ProjectProperties();
        setName(info, properties);
        setVersions(info, properties);
        var map = new HashMap<String, String>();
        properties.forEach((k, v) -> map.put((String) k, (String) v));
        //it is modifiable, so it is easy to clear it.
        info.setProperties(map);
        return info;
    }

    /**
     * Sets name.
     *
     * @param info
     * @param properties
     */
    private static void setName(ProjectProperties info, Properties properties) {
        String name = properties.getProperty("name");
        if (name == null) {
            throw new NullPointerException("Name not found");
        }
        info.setName(name);
        logger.debug("Project name: {}", info.getName());
    }

    /**
     * Sets version.
     *
     * @param info
     * @param properties
     */
    private static void setVersions(ProjectProperties info, Properties properties) {
        String versionStr = properties.getProperty("version");
        if (versionStr == null) {
            throw new NullPointerException("Version not found");
        }
        var version = new Version(versionStr);
        info.setVersion(version);
        logger.debug("Project version: {}", version.getFull());
    }

    private String name;

    private Version version;

    /**
     * All properties from file.
     */
    private Map<String, String> properties;

    /**
     * Constructor.
     */
    public ProjectProperties() {

    }

    public String getName() {
        return name;
    }

    public Version getVersion() {
        return version;
    }

    /**
     * Returns properties.
     * @return
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    /**
     * Sets properties.
     * @param properties
     */
    protected void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
