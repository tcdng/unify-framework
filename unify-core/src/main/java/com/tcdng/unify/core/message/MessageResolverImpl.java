/*
 * Copyright 2018-2024 The Code Department.
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
 * Default implementation of a message resolver.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_MESSAGE_RESOLVER)
public class MessageResolverImpl extends AbstractUnifyComponent implements MessageResolver {

    @Override
    public String resolveApplicationMessage(String message, Object... params) throws UnifyException {
        return super.resolveApplicationMessage(message, params);
    }

    @Override
    public String resolveSessionMessage(String message, Object... params) throws UnifyException {
        return super.resolveSessionMessage(message, params);
    }

    @Override
    public String resolveMessage(Locale locale, String message, Object... params) throws UnifyException {
        return super.resolveMessage(locale, message, params);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
