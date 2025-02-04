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

package com.techsenger.toolkit.core.version;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author Pavel Castornii
 */
public class Version implements Serializable {

    private Integer major;

    private Integer minor;

    private Integer patch;

    private boolean snapshot;

    private String full;

    public Version(Integer major, Integer minor, Integer patch, boolean snapshot) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.snapshot = snapshot;
        var sb = new StringBuilder();
        if (this.major != null) {
            sb.append(this.major);
            if (this.minor != null) {
                sb.append(".");
                sb.append(this.minor);
                if (this.patch != null) {
                    sb.append(".");
                    sb.append(this.patch);
                }
            }
            if (this.snapshot) {
                sb.append("-SNAPSHOT");
            }
            this.full = sb.toString();
        }
    }

    public Version(final String full) {
        var str = full.trim();
        this.full = str;
        if (str.endsWith("-SNAPSHOT") || str.toLowerCase().endsWith("-snapshot")) {
            str = str.substring(0, str.length() - "-SNAPSHOT".length());
            this.snapshot = true;
        }
        var versions = str.split(Pattern.quote("."));
        if (versions.length >= 1) {
            this.major = Integer.parseInt(versions[0]);
        }
        if (versions.length >= 2) {
            this.minor = Integer.parseInt(versions[1]);
        }
        if (versions.length == 3) {
            this.patch = Integer.parseInt(versions[2]);
        }
    }

    public Version() {

    }

    public Integer getMajor() {
        return major;
    }

    public Integer getMinor() {
        return minor;
    }

    public Integer getPatch() {
        return patch;
    }

    public boolean isSnapshot() {
        return snapshot;
    }

    public String getFull() {
        return full;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.major);
        hash = 53 * hash + Objects.hashCode(this.minor);
        hash = 53 * hash + Objects.hashCode(this.patch);
        hash = 53 * hash + (this.snapshot ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Version other = (Version) obj;
        if (this.snapshot != other.snapshot) {
            return false;
        }
        if (!Objects.equals(this.major, other.major)) {
            return false;
        }
        if (!Objects.equals(this.minor, other.minor)) {
            return false;
        }
        return Objects.equals(this.patch, other.patch);
    }

    @Override
    public String toString() {
        return full;
    }

    protected void setMajor(Integer major) {
        this.major = major;
    }

    protected void setMinor(Integer minor) {
        this.minor = minor;
    }

    protected void setPatch(Integer patch) {
        this.patch = patch;
    }

    protected void setFull(String full) {
        this.full = full;
    }

    protected void setSnapshot(boolean snapshot) {
        this.snapshot = snapshot;
    }
}
