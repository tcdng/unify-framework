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

package com.tcdng.unify.core.criterion;

import com.tcdng.unify.core.constant.TimeSeriesType;
import com.tcdng.unify.core.util.StringUtils;

/**
 * A grouping function.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class GroupingFunction {

	private String fieldName;

	private TimeSeriesType dateSeriesType;

	public GroupingFunction(String fieldName, TimeSeriesType dateSeriesType) {
		if (StringUtils.isBlank(fieldName) || dateSeriesType == null) {
			throw new IllegalArgumentException("Supplied arguments are null or blank.");
		}

		this.fieldName = fieldName;
		this.dateSeriesType = dateSeriesType;
	}

	public GroupingFunction(String fieldName) {
		if (StringUtils.isBlank(fieldName)) {
			throw new IllegalArgumentException("Supplied argument is null or blank.");
		}

		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public TimeSeriesType getDateSeriesType() {
		return dateSeriesType;
	}

	public boolean isWithFieldGrouping() {
		return dateSeriesType == null;
	}

	public boolean isWithDateFieldGrouping() {
		return dateSeriesType != null;
	}
}
