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

package com.techsenger.toolkit.rmi.server;

/**
 * This checker is used on server side to check if server must open session for owner of such
 * login name and password.
 * @author Pavel Castornii
 */
public interface RmiAccessChecker {

    /**
     * Checks loginName and password of the user.
     * @param loginName of the user.
     * @param password of the user.
     * @return false if loginName or/and password are incorrect and true if they are correct.
     */
    boolean check(String loginName, String password);
}
