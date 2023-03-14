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

import com.tcdng.unify.core.UnifyException;

/**
 * Value store reader.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ValueStoreReader {

	boolean isNull(String name) throws UnifyException;

	boolean isNotNull(String name) throws UnifyException;

	Object readScratch(String fieldName) throws UnifyException;

	<T> T readScratch(Class<T> type, String fieldName) throws UnifyException;

	Object read(String fieldName) throws UnifyException;

	<T> T read(Class<T> type, String fieldName) throws UnifyException;

	ValueStore getValueStore();

	Object getValueObject();

	int getDataIndex();

	void setDataIndex(int dataIndex);
	
    int size();

	Object getTempValue(String name) throws UnifyException;

	<T> T getTempValue(Class<T> type, String name) throws UnifyException;

	void setTempValue(String name, Object value) throws UnifyException;

	boolean isTempValue(String name);
}
