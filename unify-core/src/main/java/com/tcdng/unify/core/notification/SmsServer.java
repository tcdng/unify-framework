/*
 * Copyright 2018 The Code Department
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

import com.tcdng.unify.core.UnifyException;

/**
 * Interface for an SMS server component.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface SmsServer extends NotificationServer<SmsServerConfig> {

    /**
     * Sends an SMS.
     * 
     * @param configurationCode
     *            the code of the configuration to use
     * @param sms
     *            the sms to send
     * @throws UnifyException
     *             if configuration with code is unknown. if an error occurs
     */
    void sendSms(String configurationCode, Sms sms) throws UnifyException;

    /**
     * Sends bulk SMS.
     * 
     * @param configurationCode
     *            the code of the configuration to use
     * @param bulkSms
     *            the bulk sms to send
     * @throws UnifyException
     *             if configuration with code is unknown. if an error occurs
     */
    void sendSms(String configurationCode, BulkSms bulkSms) throws UnifyException;

}
