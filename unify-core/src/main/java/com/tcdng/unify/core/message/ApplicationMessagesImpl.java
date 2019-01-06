/*
 * Copyright 2018-2019 The Code Department.
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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Application messages implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_MESSAGES)
public class ApplicationMessagesImpl extends AbstractUnifyComponent implements ApplicationMessages {

    @Override
    public String getMessage(Locale locale, String messageKey) throws UnifyException {
        return super.getMessage(locale, messageKey);
    }

    @Override
    public String getMessage(Locale locale, String messageKey, Object... params) throws UnifyException {
        return super.getMessage(locale, messageKey, params);
    }

    @Override
    public String getApplicationMessage(String messageKey, Object... params) throws UnifyException {
        return super.getApplicationMessage(messageKey, params);
    }

    @Override
    public String getSessionMessage(String messageKey, Object... params) throws UnifyException {
        return super.getSessionMessage(messageKey, params);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
