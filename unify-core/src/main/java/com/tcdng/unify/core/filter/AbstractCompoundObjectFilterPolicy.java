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
package com.tcdng.unify.core.filter;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.CompoundRestriction;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.data.ValueStoreReader;

/**
 * Convenient abstract base class for compound object filter policies.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractCompoundObjectFilterPolicy implements ObjectFilterPolicy {

	@Override
	public boolean matchReader(ValueStoreReader reader, Restriction restriction) throws UnifyException {
		return doMatchReader(reader, ((CompoundRestriction) restriction).getRestrictionList());
	}

	@Override
	public boolean matchObject(Object bean, Restriction restriction) throws UnifyException {
		return doMatchObject(bean, ((CompoundRestriction) restriction).getRestrictionList());
	}

	protected abstract boolean doMatchReader(ValueStoreReader reader, List<Restriction> restrictionList)
			throws UnifyException;

	protected abstract boolean doMatchObject(Object bean, List<Restriction> restrictionList) throws UnifyException;
}
