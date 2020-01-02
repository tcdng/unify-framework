/*
 * Copyright 2018-2020 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.unify.core.security;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Password generator.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface PasswordGenerator extends UnifyComponent {

    /**
     * Generates a password for supplied principal.
     * 
     * @param principal
     *            the principal to generate password for
     * @param length
     *            the password length
     * @return the generated password
     * @throws UnifyException
     *             if an error occurs
     */
    String generatePassword(String principal, int length) throws UnifyException;
}
