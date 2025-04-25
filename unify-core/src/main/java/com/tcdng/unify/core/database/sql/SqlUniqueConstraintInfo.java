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
package com.tcdng.unify.core.database.sql;

import java.util.Collections;
import java.util.List;

/**
 * Unique constraint information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class SqlUniqueConstraintInfo implements SqlUniqueConstraintSchemaInfo {

	private String name;

	private List<String> fieldNameList;

	private List<SqlQueryRestrictionInfo> conditionList;

	public SqlUniqueConstraintInfo(String name, List<String> fieldNameList,
			List<SqlQueryRestrictionInfo> conditionList) {
		this.name = name;
		this.fieldNameList = Collections.unmodifiableList(fieldNameList);
		this.conditionList = Collections.unmodifiableList(conditionList);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public List<String> getFieldNameList() {
		return fieldNameList;
	}

	@Override
	public boolean isWithConditionList() {
		return !conditionList.isEmpty();
	}

	@Override
	public List<SqlQueryRestrictionInfo> getConditionList() {
		return conditionList;
	}
}
