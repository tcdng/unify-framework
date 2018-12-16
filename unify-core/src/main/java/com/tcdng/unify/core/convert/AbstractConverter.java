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
package com.tcdng.unify.core.convert;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;

/**
 * Base class for converters.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractConverter<T> implements Converter<T> {

    @Override
    public T convert(Object value, Formatter<?> formatter) throws UnifyException {
        try {
            return doConvert(value, formatter);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.CONVERTER_EXCEPTION);
        }
    }

    /**
     * Performs actual conversion.
     * 
     * @param value
     *            the value to convert
     * @param formatter
     *            the formatter if any
     * @return the conversion result
     * @throws Exception
     *             if an error occurs
     */
    protected abstract T doConvert(Object value, Formatter<?> formatter) throws Exception;
}
