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
package com.tcdng.unify.core;

import com.tcdng.unify.core.data.AlternativePrivilege;
import com.tcdng.unify.core.data.FactoryMap;

/**
 * Convenient abstract base class for alternative privilege providers
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractAlternativePrivilegeProvider extends AbstractUnifyComponent
		implements AlternativePrivilegeProvider {

	private FactoryMap<String, AlternativePrivilege> alternatives;
	
	public AbstractAlternativePrivilegeProvider() {
		this.alternatives = new FactoryMap<String, AlternativePrivilege>() {

			@Override
			protected AlternativePrivilege create(String privilege, Object... params) throws Exception {
				return createAlternatePrivilege(privilege);
			}
			
		};
	}
	
	@Override
	public AlternativePrivilege getAlternativePrivilege(String privilege) throws UnifyException {
		return alternatives.get(privilege);
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected abstract AlternativePrivilege createAlternatePrivilege(String privilege) throws UnifyException;
}
