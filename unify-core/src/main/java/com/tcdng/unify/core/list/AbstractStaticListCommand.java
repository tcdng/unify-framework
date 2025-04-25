/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.core.list;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Abstract base component for a static list list command.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractStaticListCommand extends AbstractZeroParamsListCommand {

	private FactoryMap<Locale, List<? extends Listable>> lists;
	
	public AbstractStaticListCommand() {
		this.lists = new FactoryMap<Locale, List<? extends Listable>>() {

			@Override
			protected List<? extends Listable> create(Locale locale, Object... params) throws Exception {
				String keyValueList = getMessage(locale, getName());
				return Collections.unmodifiableList(StringUtils.readStaticList(keyValueList));
			}
			
		};
	}
	
	@Override
	public List<? extends Listable> execute(Locale locale, ZeroParams param) throws UnifyException {
		return lists.get(locale);
	}

}
