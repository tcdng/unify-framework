/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.core.business;

import java.util.List;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.operation.Update;

/**
 * Generic business module implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Transactional
@Component(ApplicationComponents.APPLICATION_GENERICBUSINESSMODULE)
public class GenericBusinessModuleImpl extends AbstractBusinessModule implements GenericBusinessModule {

	@Override
	public Object create(Entity record) throws UnifyException {
		return this.db().create(record);
	}

	@Override
	public <T extends Entity> T find(Class<T> clazz, Object id) throws UnifyException {
		Query<T> query = new Query<T>(clazz);
		query.equals("id", id);
		return (T) this.db().list(query);
	}

	@Override
	public <T extends Entity> List<T> listAll(Query<T> query) throws UnifyException {
		return this.db().listAll(query);
	}

	@Override
	public <T, U extends Entity> T listValue(Class<T> valueClazz, Class<U> recordClazz, Object id, String property)
			throws UnifyException {
		return this.db().value(valueClazz, property, new Query<U>(recordClazz).equals("id", id));
	}

	@Override
	public int update(Entity record) throws UnifyException {
		return this.db().updateByIdVersion(record);
	}

	@Override
	public int updateById(Class<? extends Entity> clazz, Object id, Update update) throws UnifyException {
		return this.db().updateById(clazz, id, update);
	}

	@Override
	public int updateAll(Query<? extends Entity> query, Update update) throws UnifyException {
		return this.db().updateAll(query, update);
	}

	@Override
	public <T extends Entity> int delete(Class<T> clazz, Object id) throws UnifyException {
		return this.db().delete(clazz, id);
	}

	@Override
	public int deleteAll(Query<? extends Entity> query) throws UnifyException {
		return this.db().deleteAll(query);
	}

	@Override
	public void populateListOnly(Entity record) throws UnifyException {
		this.db().populateListOnly(record);
	}

	@Override
	public <T extends Entity> int countAll(Query<T> query) throws UnifyException {
		return this.db().countAll(query);
	}

}
