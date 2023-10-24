/*
 * Copyright 2018-2023 The Code Department.
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

package com.tcdng.unify.core.data;

import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;

/**
 * Value store writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ValueStoreWriter {
    
    void writeScratch(String fieldName, Object value) throws UnifyException;

    void write(String fieldName, Object value) throws UnifyException;
    
    void write(String fieldName, Object value, Formatter<?> formatter) throws UnifyException;

	ValueStore getValueStore();
    
    Object getValueObject();

    Object getTempValue(String name) throws UnifyException;

    <T> T getTempValue(Class<T> type, String name) throws UnifyException;

    void setTempValue(String name, Object value) throws UnifyException;

    void setTempValues(Map<String, Object> values) throws UnifyException;

    boolean isTempValue(String name);
}
