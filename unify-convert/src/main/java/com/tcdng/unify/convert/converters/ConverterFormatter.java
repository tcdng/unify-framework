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

package com.tcdng.unify.convert.converters;

/**
 * Converter formatter interface.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ConverterFormatter<T> {

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
     * @throws Exception
     *             if an error occurs
     */
    String format(T value) throws Exception;

    /**
     * Parses a formatted string to a value.
     * 
     * @param string
     *            the string to parse
     * @return the resulting value
     * @throws Exception
     *             if an error occurs
     */
    T parse(String string) throws Exception;
    
    /**
     * Returns true if applies to label
     */
    boolean isLabelFormat();
    
    /**
     * Returns true if applies to arrays
     */
    boolean isArrayFormat();
}
