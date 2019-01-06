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

import java.text.ParseException;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;

/**
 * Default cent formatter implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = "centformat", description = "$m{format.cent}")
@UplAttributes({ @UplAttribute(name = "precision", type = int.class),
        @UplAttribute(name = "scale", type = int.class, defaultValue = "0"),
        @UplAttribute(name = "useGrouping", type = boolean.class, defaultValue = "false") })
public class CentFormatterImpl extends AbstractNumberFormatter<Number> implements CentFormatter {

    public CentFormatterImpl() {
        super(Number.class, NumberType.INTEGER);
    }

    @Override
    public String format(Number value) throws UnifyException {
        return super.format(Double.valueOf(value.doubleValue() * 100));
    }

    @Override
    public Number parse(String string) throws UnifyException {
        try {
            Number value = getNumberFormat().parse(string);
            return Double.valueOf(value.doubleValue() / 100);
        } catch (ParseException e) {
            throwOperationErrorException(e);
        }
        return null;
    }

}
