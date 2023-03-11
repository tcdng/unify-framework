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

package com.tcdng.unify.core.database.dynamic;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.DynamicFieldType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.EntityFieldType;

/**
 * Dynamic list-only field information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DynamicListOnlyFieldInfo extends DynamicFieldInfo {

	private DynamicFieldInfo propertyFieldInfo;

	private String key;

	private String property;

	private boolean resolved;

	public DynamicListOnlyFieldInfo(DynamicFieldType type, DynamicFieldInfo propertyFieldInfo, String columnName,
			String fieldName, String key, String property, boolean descriptive, boolean resolved) {
		super(type, EntityFieldType.LIST_ONLY, propertyFieldInfo.getDataType(), columnName, fieldName,
				null, propertyFieldInfo.getEnumClassName(), descriptive, false);
		this.propertyFieldInfo = propertyFieldInfo;
		this.key = key;
		this.property = property;
		this.resolved = resolved;
	}

	public DynamicListOnlyFieldInfo(DynamicFieldType type, String columnName, String fieldName, String key,
			String property, boolean descriptive) {
		super(type, EntityFieldType.LIST_ONLY, DataType.STRING, columnName, fieldName, null, null, descriptive, false);
		this.key = key;
		this.property = property;
		this.resolved = true;
	}

	public DynamicFieldInfo getPropertyFieldInfo() {
		return propertyFieldInfo;
	}

	public String getKey() {
		return key;
	}

	public String getProperty() {
		return property;
	}
    
    protected Resolution doFinalizeResolution() throws UnifyException {
		if (!resolved) {
			synchronized (this) {
				if (!resolved) {
					DynamicEntityInfo parentDynamicEntityInfo = ((DynamicForeignKeyFieldInfo) propertyFieldInfo).getParentDynamicEntityInfo();
					parentDynamicEntityInfo.finalizeResolution();
					propertyFieldInfo = parentDynamicEntityInfo.getDynamicFieldInfo(property);
					resolved = true;
			    	return new Resolution(propertyFieldInfo.getDataType(), propertyFieldInfo.getEnumClassName());
				}
			}
		}
		
    	return super.doFinalizeResolution();
    }

}
