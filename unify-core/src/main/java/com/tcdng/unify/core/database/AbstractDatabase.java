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
package com.tcdng.unify.core.database;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.criterion.AggregateFunction;
import com.tcdng.unify.core.criterion.GroupingFunction;
import com.tcdng.unify.core.criterion.Update;

/**
 * Abstract implementation of a database. Also implements transaction management
 * methods of a database transaction manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractDatabase extends AbstractUnifyComponent implements Database {

    @Configurable
    private DatabaseTransactionManager databaseTransactionManager;

    @Override
    public void joinTransaction() throws UnifyException {
        databaseTransactionManager.joinTransaction(this);
    }

    @Override
    public <T extends Entity> T find(Class<T> clazz, Object pk) throws UnifyException {
        return getDatabaseSession().find(clazz, pk);
    }

    @Override
    public <T extends Entity> T find(Class<T> clazz, Object pk, Object versionNo) throws UnifyException {
        return getDatabaseSession().find(clazz, pk, versionNo);
    }

    @Override
    public <T extends Entity> T find(Query<T> query) throws UnifyException {
        return getDatabaseSession().find(query);
    }

    @Override
	public <T extends Entity> T findFirst(Query<T> query) throws UnifyException {
        return getDatabaseSession().findFirst(query);
	}

	@Override
	public <T extends Entity> T findLast(Query<T> query) throws UnifyException {
        return getDatabaseSession().findLast(query);
	}

	@Override
    public <T extends Entity> T findLean(Class<T> clazz, Object pk) throws UnifyException {
        return getDatabaseSession().findLean(clazz, pk);
    }

    @Override
    public <T extends Entity> T findLean(Class<T> clazz, Object pk, Object versionNo) throws UnifyException {
        return getDatabaseSession().findLean(clazz, pk, versionNo);
    }

    @Override
    public <T extends Entity> T findLean(Query<T> query) throws UnifyException {
        return getDatabaseSession().findLean(query);
    }

    @Override
    public <T extends Entity> T findLeanFirst(Query<T> query) throws UnifyException {
        return getDatabaseSession().findLeanFirst(query);
    }

    @Override
    public <T extends Entity> T findLeanLast(Query<T> query) throws UnifyException {
        return getDatabaseSession().findLeanLast(query);
    }

    @Override
    public <T extends Entity> T findConstraint(T record) throws UnifyException {
        return getDatabaseSession().findConstraint(record);
    }

    @Override
    public <T extends Entity> List<T> findAll(Query<T> query) throws UnifyException {
        return getDatabaseSession().findAll(query);
    }

    @Override
    public <T extends Entity> List<T> findAllWithChildren(Query<T> query) throws UnifyException {
        return getDatabaseSession().findAllWithChildren(query);
    }

    @Override
    public <T, U extends Entity> Map<T, U> findAllMap(Class<T> keyClass, String keyName, Query<U> query)
            throws UnifyException {
        return getDatabaseSession().findAllMap(keyClass, keyName, query);
    }

    @Override
    public <T, U extends Entity> Map<T, List<U>> findAllListMap(Class<T> keyClass, String keyName, Query<U> query)
            throws UnifyException {
        return getDatabaseSession().findAllListMap(keyClass, keyName, query);
    }

    @Override
    public <T extends Entity> void findChildren(T record) throws UnifyException {
        getDatabaseSession().findChildren(record);
    }

    @Override
	public <T extends Entity> void findEditableChildren(T record) throws UnifyException {
        getDatabaseSession().findEditableChildren(record);
	}

	@Override
	public <T extends Entity> void findReadOnlyChildren(T record) throws UnifyException {
        getDatabaseSession().findReadOnlyChildren(record);
	}

	@Override
    public <T extends Entity> T list(Class<T> clazz, Object pk) throws UnifyException {
        return getDatabaseSession().list(clazz, pk);
    }

    @Override
    public <T extends Entity> T list(Class<T> clazz, Object pk, Object versionNo) throws UnifyException {
        return getDatabaseSession().list(clazz, pk, versionNo);
    }

    @Override
    public <T extends Entity> T list(Query<T> query) throws UnifyException {
        return getDatabaseSession().list(query);
    }

    @Override
    public <T extends Entity> T listFirst(Query<T> query) throws UnifyException {
        return getDatabaseSession().listFirst(query);
    }

    @Override
    public <T extends Entity> T listLast(Query<T> query) throws UnifyException {
        return getDatabaseSession().listLast(query);
    }

    @Override
    public <T extends Entity> T listLean(Class<T> clazz, Object pk) throws UnifyException {
        return getDatabaseSession().listLean(clazz, pk);
    }

    @Override
    public <T extends Entity> T listLean(Class<T> clazz, Object pk, Object versionNo) throws UnifyException {
        return getDatabaseSession().listLean(clazz, pk, versionNo);
    }

    @Override
    public <T extends Entity> T listLean(Query<T> query) throws UnifyException {
        return getDatabaseSession().listLean(query);
    }

    @Override
    public <T extends Entity> T listLeanFirst(Query<T> query) throws UnifyException {
        return getDatabaseSession().listLeanFirst(query);
    }

    @Override
    public <T extends Entity> T listLeanLast(Query<T> query) throws UnifyException {
        return getDatabaseSession().listLeanLast(query);
    }

    @Override
    public <T extends Entity> List<T> listAll(Query<T> query) throws UnifyException {
        return getDatabaseSession().listAll(query);
    }

    @Override
    public <T extends Entity> List<T> listAllWithChildren(Query<T> query) throws UnifyException {
        return getDatabaseSession().listAllWithChildren(query);
    }

    @Override
    public <T, U extends Entity> Map<T, U> listAllMap(Class<T> keyClass, String keyName, Query<U> query)
            throws UnifyException {
        return getDatabaseSession().listAll(keyClass, keyName, query);
    }

    @Override
    public <T, U extends Entity> Map<T, List<U>> listAllListMap(Class<T> keyClass, String keyName, Query<U> query)
            throws UnifyException {
        return getDatabaseSession().listAllListMap(keyClass, keyName, query);
    }

    @Override
    public <T extends Entity> void listChildren(T record) throws UnifyException {
        getDatabaseSession().listChildren(record);
    }

    @Override
	public <T extends Entity> void listEditableChildren(T record) throws UnifyException {
        getDatabaseSession().listEditableChildren(record);
	}

	@Override
	public <T extends Entity> void listReadOnlyChildren(T record) throws UnifyException {
        getDatabaseSession().listReadOnlyChildren(record);
	}

	@Override
    public <T, U extends Entity> List<T> valueList(Class<T> fieldClass, String fieldName, Query<U> query)
            throws UnifyException {
        return getDatabaseSession().valueList(fieldClass, fieldName, query);
    }

    @Override
    public <T, U extends Entity> T value(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException {
        return getDatabaseSession().value(fieldClass, fieldName, query);
    }

    @Override
	public <T, U extends Entity> Optional<T> valueOptional(Class<T> fieldClass, String fieldName, Query<U> query)
			throws UnifyException {
        return getDatabaseSession().valueOptional(fieldClass, fieldName, query);
	}

	@Override
    public <T extends Number, U extends Entity> T min(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException {
        return getDatabaseSession().min(fieldClass, fieldName, query);
    }

    @Override
    public <T extends Number, U extends Entity> T max(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException {
        return getDatabaseSession().max(fieldClass, fieldName, query);
    }

    @Override
	public <T extends Number, U extends Entity> int add(Class<T> fieldClass, String fieldName, T val, Query<U> query)
			throws UnifyException {
		return getDatabaseSession().add(fieldClass, fieldName, val, query);
	}

	@Override
	public <T extends Number, U extends Entity> int subtract(Class<T> fieldClass, String fieldName, T val,
			Query<U> query) throws UnifyException {
		return getDatabaseSession().subtract(fieldClass, fieldName, val, query);
	}

	@Override
	public <T extends Number, U extends Entity> int multiply(Class<T> fieldClass, String fieldName, T val,
			Query<U> query) throws UnifyException {
		return getDatabaseSession().multiply(fieldClass, fieldName, val, query);
	}

	@Override
	public <T extends Number, U extends Entity> int divide(Class<T> fieldClass, String fieldName, T val, Query<U> query)
			throws UnifyException {
		return getDatabaseSession().divide(fieldClass, fieldName, val, query);
	}

	@Override
    public <T, U extends Entity> Set<T> valueSet(Class<T> fieldClass, String fieldName, Query<U> query)
            throws UnifyException {
        return getDatabaseSession().valueSet(fieldClass, fieldName, query);
    }

    @Override
    public <T, U, V extends Entity> Map<T, U> valueMap(Class<T> keyClass, String keyName, Class<U> valueClass,
            String valueName, Query<V> query) throws UnifyException {
        return getDatabaseSession().valueMap(keyClass, keyName, valueClass, valueName, query);
    }

    @Override
    public <T, U, V extends Entity> Map<T, List<U>> valueListMap(Class<T> keyClass, String keyName, Class<U> valueClass,
            String valueName, Query<V> query) throws UnifyException {
        return getDatabaseSession().valueListMap(keyClass, keyName, valueClass, valueName, query);
    }

    @Override
    public void populateListOnly(Entity record) throws UnifyException {
        getDatabaseSession().populateListOnly(record);
    }

    @Override
    public Object create(Entity record) throws UnifyException {
        return getDatabaseSession().create(record);
    }

    @Override
	public int update(NativeUpdate update) throws UnifyException {
    	return getDatabaseSession().update(update);
	}

    @Override
    public int updateById(Entity record) throws UnifyException {
        return getDatabaseSession().updateById(record);
    }

    @Override
    public int updateByIdVersion(Entity record) throws UnifyException {
        return getDatabaseSession().updateByIdVersion(record);
    }

    @Override
	public int updateByIdEditableChildren(Entity record) throws UnifyException {
        return getDatabaseSession().updateByIdEditableChildren(record);
	}

	@Override
	public int updateByIdVersionEditableChildren(Entity record) throws UnifyException {
        return getDatabaseSession().updateByIdVersionEditableChildren(record);
	}

	@Override
    public int updateLeanById(Entity record) throws UnifyException {
        return getDatabaseSession().updateLeanById(record);
    }

    @Override
    public int updateLeanByIdVersion(Entity record) throws UnifyException {
        return getDatabaseSession().updateLeanByIdVersion(record);
    }

    @Override
    public int updateById(Class<? extends Entity> clazz, Object id, Update update) throws UnifyException {
        return getDatabaseSession().updateById(clazz, id, update);
    }

    @Override
    public int updateAll(Query<?> query, Update update) throws UnifyException {
        return getDatabaseSession().updateAll(query, update);
    }

    @Override
    public int deleteById(Entity record) throws UnifyException {
        return getDatabaseSession().deleteById(record);
    }

    @Override
    public int deleteByIdVersion(Entity record) throws UnifyException {
        return getDatabaseSession().deleteByIdVersion(record);
    }

    @Override
    public int delete(Class<? extends Entity> clazz, Object pk) throws UnifyException {
        return getDatabaseSession().delete(clazz, pk);
    }

    @Override
    public int deleteAll(Query<?> query) throws UnifyException {
        return getDatabaseSession().deleteAll(query);
    }

    @Override
    public <T extends Entity> int countAll(Query<T> query) throws UnifyException {
        return getDatabaseSession().count(query);
    }

    @Override
    public Aggregation aggregate(AggregateFunction aggregateFunction, Query<? extends Entity> query)
            throws UnifyException {
        return getDatabaseSession().aggregate(aggregateFunction, query);
    }

    @Override
	public List<Aggregation> aggregate(List<AggregateFunction> aggregateFunction, Query<? extends Entity> query)
			throws UnifyException {
        return getDatabaseSession().aggregate(aggregateFunction, query);
	}

    @Override
	public List<GroupingAggregation> aggregate(AggregateFunction aggregateFunction, Query<? extends Entity> query,
			GroupingFunction groupingFunction) throws UnifyException {
        return getDatabaseSession().aggregate(aggregateFunction, query, groupingFunction);
	}

	@Override
	public List<GroupingAggregation> aggregate(List<AggregateFunction> aggregateFunction, Query<? extends Entity> query,
			GroupingFunction groupingFunction) throws UnifyException {
        return getDatabaseSession().aggregate(aggregateFunction, query, groupingFunction);
	}

	@Override
	public List<GroupingAggregation> aggregate(AggregateFunction aggregateFunction, Query<? extends Entity> query,
			List<GroupingFunction> groupingFunction) throws UnifyException {
		return getDatabaseSession().aggregate(aggregateFunction, query, groupingFunction);
	}

	@Override
	public List<GroupingAggregation> aggregate(List<AggregateFunction> aggregateFunction, Query<? extends Entity> query,
			List<GroupingFunction> groupingFunction) throws UnifyException {
		return getDatabaseSession().aggregate(aggregateFunction, query, groupingFunction);
	}

	@Override
    public Entity getExtendedInstance(Class<? extends Entity> entityClass) throws UnifyException {
        return getDatabaseSession().getExtendedInstance(entityClass);
    }

    @Override
    public Date getNow() throws UnifyException {
        return getDatabaseSession().getNow();
    }

    @Override
    public void executeCallable(CallableProc callableProc) throws UnifyException {
        getDatabaseSession().executeCallable(callableProc);
    }

    @Override
    public Map<Class<?>, List<?>> executeCallableWithResults(CallableProc callableProc) throws UnifyException {
        return getDatabaseSession().executeCallableWithResults(callableProc);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private DatabaseSession getDatabaseSession() throws UnifyException {
        return databaseTransactionManager.getDatabaseSession(this);
    }
}
