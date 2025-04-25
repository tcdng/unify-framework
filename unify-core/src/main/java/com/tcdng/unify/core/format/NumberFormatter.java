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

import com.tcdng.unify.core.UnifyException;

/**
 * Number formatter component interface.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface NumberFormatter<T extends Number> extends Formatter<T> {
    /**
     * Gets the formatter number precision.
     */
    int getPrecision() throws UnifyException;

    /**
     * Sets the formatter number precision.
     */
    void setPrecision(int precision);

    /**
     * Gets the formatter number scale.
     */
    int getScale() throws UnifyException;

    /**
     * Sets the formatter number scale.
     */
    void setScale(int scale);

    /**
     * Sets the grouping used flag.
     */
    void setGroupingUsed(boolean groupingUsed);

    /**
     * Gets the grouping used flag.
     */
    boolean isGroupingUsed() throws UnifyException;

    /**
     * Sets the strict format flag.
     */
    void setStrictFormat(boolean strictFormat);

    /**
     * Gets the strict format flag.
     */
    boolean isStrictFormat() throws UnifyException;

    /**
     * Gets the formatter number symbols.
     */
    NumberSymbols getNumberSymbols() throws UnifyException;
}
