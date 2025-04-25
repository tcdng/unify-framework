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

package com.tcdng.unify.core.message;

import java.util.Locale;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Application messages component.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ApplicationMessages extends UnifyComponent {
    /**
     * Gets a message resource by locale and message key.
     * 
     * @param locale
     *            the locale
     * @param messageKey
     *            the message key
     * @return the message resource value
     * @throws UnifyException
     *             if resource is missing. If an error occurs
     */
    String getMessage(Locale locale, String messageKey) throws UnifyException;

    /**
     * Gets a formatted message resource by locale and message key.
     * 
     * @param locale
     *            the locale
     * @param messageKey
     *            the message key
     * @param params
     *            optional formatting parameters
     * @return the message resource value
     * @throws UnifyException
     *             if resource is missing. If an error occurs
     */
    String getMessage(Locale locale, String messageKey, Object... params) throws UnifyException;

    /**
     * Gets a formatted message resource by application locale and message key.
     * 
     * @param messageKey
     *            the message key
     * @param params
     *            optional formatting parameters
     * @return the message resource value
     * @throws UnifyException
     *             if resource is missing. If an error occurs
     */
    String getApplicationMessage(String messageKey, Object... params) throws UnifyException;

    /**
     * Gets a formatted message resource by session locale and message key.
     * 
     * @param messageKey
     *            the message key
     * @param params
     *            optional formatting parameters
     * @return the message resource value
     * @throws UnifyException
     *             if resource is missing. If an error occurs
     */
    String getSessionMessage(String messageKey, Object... params) throws UnifyException;
}
