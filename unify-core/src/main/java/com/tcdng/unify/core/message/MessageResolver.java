/*
 * Copyright 2018-2022 The Code Department.
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
 * Message resolver.
 * 
 * @author The Code Department
 * @since 1.0
 */

public interface MessageResolver extends UnifyComponent {

    /**
     * Resolves message using application locale.
     * 
     * @param message
     *                the message to resolve
     * @param params
     *                optional parameters
     * @return the resolved message
     * @throws UnifyException
     *                        if an error occurs
     */
    String resolveApplicationMessage(String message, Object... params) throws UnifyException;

    /**
     * Resolves message using session locale.
     * 
     * @param message
     *                the message to resolve
     * @param params
     *                optional parameters
     * @return the resolved message
     * @throws UnifyException
     *                        if an error occurs
     */
    String resolveSessionMessage(String message, Object... params) throws UnifyException;

    /**
     * Resolves message using supplied locale.
     * 
     * @param locale
     *                the locale to use
     * @param message
     *                the message to resolve
     * @param params
     *                optional parameters
     * @return the resolved message
     * @throws UnifyException
     *                        if an error occurs
     */
    String resolveMessage(Locale locale, String message, Object... params) throws UnifyException;

}
