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
package com.tcdng.unify.core.operation;

/**
 * Criteria builder which uses logical conjunction for building a criteria.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class AndBuilder extends CriteriaBuilder {

	public AndBuilder and(CriteriaBuilder criteriaBuilder) {
		add(criteriaBuilder.getCriteria());
		return this;
	}

	public AndBuilder and(Criteria criteria) {
		add(criteria);
		return this;
	}

	protected CriteriaBuilder add(Criteria criteria) {
		if (this.criteria == null) {
			this.criteria = criteria;
		} else {
			this.criteria = new And(this.criteria, criteria);
		}
		return this;
	}
}
