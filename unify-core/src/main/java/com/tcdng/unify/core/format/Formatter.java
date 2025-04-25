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

import java.util.Locale;

import com.tcdng.unify.convert.converters.ConverterFormatter;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.upl.UplComponent;

/**
 * A formatter component for formatting a value to a string and also parsing a
 * string back to a value.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface Formatter<T> extends UplComponent, ConverterFormatter<T> {

    /**
     * Returns the formatter format helper.
     */
    FormatHelper getFormatHelper() throws UnifyException;

    /**
     * Returns the format data type.
     */
    Class<T> getDataType();

    /**
     * Formats a value to a string.
     * 
     * @param value
     *            the value to format
     * @return the formatted value
     * @throws UnifyException
     *             if an error occurs
     */
    String format(T value) throws UnifyException;

	/**
	 * Return true if formatter is strict
	 * 
	 * @throws UnifyException if an error occurs
	 */
	boolean isStrictFormat() throws UnifyException;
    
    /**
     * Parses a formatted string to a value.
     * 
     * @param string
     *            the string to parse
     * @return the resulting value
     * @throws UnifyException
     *             if an error occurs
     */
    T parse(String string) throws UnifyException;

    /**
     * Returns the formatter format pattern.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getPattern() throws UnifyException;

    /**
     * Returns the formatter locale.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    Locale getLocale() throws UnifyException;
}
