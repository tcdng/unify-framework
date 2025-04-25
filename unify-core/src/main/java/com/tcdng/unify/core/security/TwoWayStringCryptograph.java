/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.transform.Transformer;

/**
 * Used for encoding and decoding string messages.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface TwoWayStringCryptograph extends Transformer<String, String> {
    /**
     * Encrypts a string.
     * 
     * @param string
     *            the string to encrypt
     * @return the encrypted string
     * @throws UnifyException
     *             if an error occurs
     */
    String encrypt(String string) throws UnifyException;

    /**
     * Decrypts a string.
     * 
     * @param string
     *            the string to decrypt
     * @return the decrypted string
     * @throws UnifyException
     *             if an error occurs
     */
    String decrypt(String string) throws UnifyException;
}
