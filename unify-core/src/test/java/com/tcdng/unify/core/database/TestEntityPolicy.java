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
package com.tcdng.unify.core.database;

import java.util.Date;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Policy class for test record.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("testentity-policy")
public class TestEntityPolicy extends AbstractEntityPolicy {

	private long idCounter;

	@Override
	public Object preCreate(Entity record, Date now) throws UnifyException {
		Long id = Long.valueOf(++idCounter);
		((AbstractTestEntity) record).setId(id);
		((AbstractTestEntity) record).setVersion(1L);
		return id;
	}

	@Override
	public void preUpdate(Entity record, Date now) throws UnifyException {
		((AbstractTestEntity) record).setVersion(((AbstractTestEntity) record).getVersion() + 1L);
	}

	@Override
	public void preDelete(Entity record, Date now) throws UnifyException {

	}

	@Override
	public void onCreateError(Entity record) {

	}

	@Override
	public void onUpdateError(Entity record) {

	}

	@Override
	public void onDeleteError(Entity record) {

	}

	@Override
	public boolean isSetNow() {
		return false;
	}
}
