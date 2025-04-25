/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.core.logging;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * A component for logging messages and exceptions.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface Logger extends UnifyComponent {
    /**
     * Logs a message.
     * 
     * @param loggingLevel
     *            the logging level
     * @param message
     *            the message to log
     * @throws UnifyException
     *             if an error occurs
     */
    void log(LoggingLevel loggingLevel, String message) throws UnifyException;

    /**
     * Logs a message with accompanying exception.
     * 
     * @param loggingLevel
     *            the logging level
     * @param message
     *            the message to log
     * @param exception
     *            exception to log
     * @throws UnifyException
     *             if an error occurs
     */
    void log(LoggingLevel loggingLevel, String message, Exception exception) throws UnifyException;

    /**
     * Returns true value if supplied logging level is enabled.
     * 
     * @param loggingLevel
     *            the logging level
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isEnabled(LoggingLevel loggingLevel) throws UnifyException;
}
