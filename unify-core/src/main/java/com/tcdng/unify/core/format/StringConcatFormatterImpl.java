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
package com.tcdng.unify.core.format;

import java.lang.reflect.Array;
import java.util.Collection;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;

/**
 * Default implementation of a string concatenation formatter.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(name = "stringconcatformat", description = "$m{format.stringconcat}")
@UplAttributes({ @UplAttribute(name = "newline", type = boolean.class) })
public class StringConcatFormatterImpl extends AbstractFormatter<Object> implements StringConcatFormatter {

    public StringConcatFormatterImpl() {
        super(Object.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String doFormat(Object value) throws UnifyException {
        String result = null;
        if (value != null) {
            String separator = "";
            if (getUplAttribute(boolean.class, "newline")) {
                separator = "<br/>";
            }

            if (value.getClass().isArray()) {
                StringBuilder sb = new StringBuilder();
                int len = Array.getLength(value);
                boolean appendSeparator = false;
                for (int i = 0; i < len; i++) {
                    if (appendSeparator) {
                        sb.append(separator);
                    } else {
                        appendSeparator = true;
                    }
                    sb.append(String.valueOf(Array.get(value, i)));
                }
                result = sb.toString();
            } else if (value instanceof Collection) {
                StringBuilder sb = new StringBuilder();
                boolean appendSeparator = false;
                for (Object colValue : (Collection<Object>) value) {
                    if (appendSeparator) {
                        sb.append(separator);
                    } else {
                        appendSeparator = true;
                    }
                    sb.append(colValue);
                }
                result = sb.toString();
            } else {
                result = String.valueOf(value);
            }
        }

        return result;
    }

    @Override
    protected Object doParse(String string) throws UnifyException {
        return null;
    }

    @Override
    public String getPattern() throws UnifyException {
        return null;
    }

}
