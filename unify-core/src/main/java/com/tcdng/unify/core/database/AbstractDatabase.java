/*
 * Copyright 2018-2019 The Code Department.
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
import java.util.Set;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.Aggregate;
import com.tcdng.unify.core.data.AggregateType;
import com.tcdng.unify.core.operation.Update;

/**
 * Abstract implementation of a database. Also implements transaction management
 * methods of a database transaction manager.
 * 
 * @author Lateef Ojulari
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
    public <T extends Entity> T findConstraint(T record) throws UnifyException {
        return getDatabaseSession().findConstraint(record);
    }

    @Override
    public <T extends Entity> List<T> findAll(Query<T> query) throws UnifyException {
        return getDatabaseSession().findAll(query);
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
    public <T extends Entity> List<T> listAll(Query<T> query) throws UnifyException {
        return getDatabaseSession().listAll(query);
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
    public <T, U extends Entity> List<T> valueList(Class<T> fieldClass, String fieldName, Query<U> query)
            throws UnifyException {
        return getDatabaseSession().valueList(fieldClass, fieldName, query);
    }

    @Override
    public <T, U extends Entity> T value(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException {
        return getDatabaseSession().value(fieldClass, fieldName, query);
    }

    @Override
    public <T, U extends Entity> T min(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException {
        return getDatabaseSession().min(fieldClass, fieldName, query);
    }

    @Override
    public <T, U extends Entity> T max(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException {
        return getDatabaseSession().max(fieldClass, fieldName, query);
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
    public int updateById(Entity record) throws UnifyException {
        return getDatabaseSession().updateById(record);
    }

    @Override
    public int updateByIdVersion(Entity record) throws UnifyException {
        return getDatabaseSession().updateByIdVersion(record);
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
    public int countAll(Query<?> query) throws UnifyException {
        return getDatabaseSession().count(query);
    }

    @Override
    public List<Aggregate<?>> aggregate(AggregateType aggregateType, Query<?> query) throws UnifyException {
        return getDatabaseSession().aggregate(aggregateType, query);
    }

    @Override
    public Date getNow() throws UnifyException {
        return getDatabaseSession().getNow();
    }

    @Override
    public void execute(CallableProc callableProc) throws UnifyException {
        getDatabaseSession().execute(callableProc);
    }

    @Override
    public Map<Class<?>, List<?>> executeWithResults(CallableProc callableProc) throws UnifyException {
        return getDatabaseSession().executeWithResults(callableProc);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected abstract DataSource getDataSource() throws UnifyException;

    private DatabaseSession getDatabaseSession() throws UnifyException {
        return databaseTransactionManager.getDatabaseSession(this);
    }
}
