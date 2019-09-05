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
package com.tcdng.unify.core.format;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Default message formatter implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = "messageformat", description = "$m{format.message}")
public class MessageFormatterImpl extends AbstractFormatter<Object> implements MessageFormatter {

    public MessageFormatterImpl() {
        super(Object.class);
    }

    @Override
    public String format(Object value) throws UnifyException {
        return resolveSessionMessage(String.valueOf(value));
    }

    @Override
    public Object parse(String string) throws UnifyException {
        return string;
    }

    @Override
    public String getPattern() throws UnifyException {
        return null;
    }

}
