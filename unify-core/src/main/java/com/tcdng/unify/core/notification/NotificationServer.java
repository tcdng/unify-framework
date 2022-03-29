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
package com.tcdng.unify.core.notification;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Interface for a notification server component.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface NotificationServer<T extends AbstractNotifServerConfig> extends UnifyComponent {

    /**
     * Configures server replacing existing if necessary.
     * 
     * @param configurationCode
     *            the configuration code
     * @param config
     *            the email server configuration
     * @throws UnifyException
     *             if an error occurs
     */
    void configure(String configurationCode, T config) throws UnifyException;

    /**
     * Returns true if configuration with supplied code exists on server.
     * 
     * @param configurationCode
     *            the configuration code to check
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isConfigured(String configurationCode) throws UnifyException;
}
