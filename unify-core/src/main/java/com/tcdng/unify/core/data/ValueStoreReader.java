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

package com.tcdng.unify.core.data;

import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;

/**
 * Value store reader.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ValueStoreReader {

	void setFormats(Formats formats);
	
	String readAsString(String name) throws UnifyException;
	
	String readIntegerAsString(String name) throws UnifyException;
	
	String readDecimalAsString(String name) throws UnifyException;
	
	String readDateAsString(String name) throws UnifyException;
	
	String readTimestampAsString(String name) throws UnifyException;
	
	boolean isNull(String name) throws UnifyException;

	boolean isNotNull(String name) throws UnifyException;

	Object readScratch(String fieldName) throws UnifyException;

	<T> T readScratch(Class<T> type, String fieldName) throws UnifyException;

	Object read(String fieldName) throws UnifyException;

	<T> T read(Class<T> type, String fieldName) throws UnifyException;

	<T> T read(Class<T> type, String fieldName, Formatter<?> formatter) throws UnifyException;

	ValueStore getValueStore();

	Object getValueObject();

	int getDataIndex();

	void setDataIndex(int dataIndex);
	
    int size();

	Object getTempValue(String name) throws UnifyException;

	<T> T getTempValue(Class<T> type, String name) throws UnifyException;

	void setTempValue(String name, Object value) throws UnifyException;

    void setTempValues(Map<String, Object> values) throws UnifyException;

	boolean isTempValue(String name);
}
