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

package com.tcdng.unify.core.database.dynamic;

import com.tcdng.unify.core.annotation.DynamicFieldType;
import com.tcdng.unify.core.constant.DynamicEntityFieldType;

/**
 * Dynamic child list field information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class DynamicChildListFieldInfo extends DynamicFieldInfo {

	private DynamicEntityInfo childDynamicEntityInfo;

	private boolean editable;

	public DynamicChildListFieldInfo(DynamicFieldType type, DynamicEntityInfo childDynamicEntityInfo, String fieldName,
			boolean editable) {
		super(type, DynamicEntityFieldType.CHILDLIST, null, null, fieldName, null, null, false, false);
		this.childDynamicEntityInfo = childDynamicEntityInfo;
		this.editable = editable;
	}

	public DynamicEntityInfo getChildDynamicEntityInfo() {
		return childDynamicEntityInfo;
	}

	public boolean isEditable() {
		return editable;
	}

}
