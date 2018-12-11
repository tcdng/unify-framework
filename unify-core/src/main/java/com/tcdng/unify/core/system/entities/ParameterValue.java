/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.system.entities;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.UniqueConstraint;

/**
 * Parameter value entity.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Table(name = "PARAMVALUE", uniqueConstraints = { @UniqueConstraint({ "parameterValuesId", "paramKey" }) })
public class ParameterValue extends AbstractSystemSequencedEntity {

	@ForeignKey(ParameterValues.class)
	private Long parameterValuesId;

	@Column
	private String paramKey;

	@Column(length = 128, nullable = true)
	private String paramValue;

	public Long getParameterValuesId() {
		return parameterValuesId;
	}

	public void setParameterValuesId(Long parameterValuesId) {
		this.parameterValuesId = parameterValuesId;
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

}
