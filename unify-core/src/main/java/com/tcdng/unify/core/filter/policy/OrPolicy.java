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
package com.tcdng.unify.core.filter.policy;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.data.ValueStoreReader;
import com.tcdng.unify.core.filter.AbstractCompoundObjectFilterPolicy;
import com.tcdng.unify.core.util.FilterUtils;

/**
 * Logical disjunction policy.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class OrPolicy extends AbstractCompoundObjectFilterPolicy {

	@Override
	protected boolean doMatchReader(ValueStoreReader reader, List<Restriction> restrictionList) throws UnifyException {
		for (Restriction restriction : restrictionList) {
			if (FilterUtils.getBeanFilterPolicy(restriction.getConditionType()).matchReader(reader, restriction)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean doMatchObject(Object bean, List<Restriction> restrictionList) throws UnifyException {
		for (Restriction restriction : restrictionList) {
			if (FilterUtils.getBeanFilterPolicy(restriction.getConditionType()).matchObject(bean, restriction)) {
				return true;
			}
		}

		return false;
	}

}
