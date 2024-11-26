/*
 * Copyright 2018-2024 The Code Department.
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

package com.tcdng.unify.core.constant;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Entity field type.
 * 
 * @author The Code Department
 * @since 1.0
 */
@StaticList(name = "entityfieldtypelist", description = "$m{staticlist.entityfieldtypelist}")
public enum EntityFieldType implements EnumConst {

    FOREIGN_KEY("FRK"),
    TABLE_COLUMN("COL"),
    LIST_ONLY("LST"),
    CHILD("CLD"),
    CHILDLIST("CLL");

	private final String code;

	private EntityFieldType(String code) {
		this.code = code;
	}

	@Override
	public String code() {
		return this.code;
	}

	@Override
	public String defaultCode() {
		return TABLE_COLUMN.code;
	}

    public boolean isForeignKey() {
        return FOREIGN_KEY.equals(this);
    }

    public boolean isTableColumn() {
        return TABLE_COLUMN.equals(this);
    }

    public boolean isListOnly() {
        return LIST_ONLY.equals(this);
    }

    public boolean isChild() {
        return CHILD.equals(this);
    }

    public boolean isChildList() {
        return CHILDLIST.equals(this);
    }

	public static EntityFieldType fromCode(String code) {
		return EnumUtils.fromCode(EntityFieldType.class, code);
	}

	public static EntityFieldType fromName(String name) {
		return EnumUtils.fromName(EntityFieldType.class, name);
	}
}
