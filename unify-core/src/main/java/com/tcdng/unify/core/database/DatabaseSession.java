/*
 * Copyright 2018-2020 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Aggregate;
import com.tcdng.unify.core.criterion.AggregateFunction;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.data.Aggregation;
import com.tcdng.unify.core.data.GroupAggregation;

/**
 * A database session.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface DatabaseSession {

    /**
     * Creates an record in the database.
     * 
     * @param record
     *            the record to create
     * @return the record Id
     * @throws UnifyException
     *             if an error occurs during creation
     */
    Object create(Entity record) throws UnifyException;

    /**
     * Retrieves a record by ID. List-only properties of returned object are not
     * populated. Child and child list properties are populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @throws UnifyException
     *             if record is not found
     */
    <T extends Entity> T find(Class<T> clazz, Object id) throws UnifyException;

    /**
     * Retrieves a record by id and version number. List-only properties of returned
     * object are not populated. Child and child list properties are populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @param versionNo
     *            the version number
     * @throws UnifyException
     *             if record is not found
     */
    <T extends Entity> T find(Class<T> clazz, Object id, Object versionNo) throws UnifyException;

    /**
     * Retrieves a record by query. List-only properties of returned object are not
     * populated. Child and child list properties are populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T find(Query<T> query) throws UnifyException;

    /**
     * Retrieves a record by ID. List-only properties of returned object are not
     * populated. Child and child list properties are not populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @throws UnifyException
     *             if record is not found
     */
    <T extends Entity> T findLean(Class<T> clazz, Object id) throws UnifyException;

    /**
     * Retrieves a record by id and version number. List-only properties of returned
     * object are not populated. Child and child list properties are not populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @param versionNo
     *            the version number
     * @throws UnifyException
     *             if record is not found
     */
    <T extends Entity> T findLean(Class<T> clazz, Object id, Object versionNo) throws UnifyException;

    /**
     * Retrieves a record by query. List-only properties of returned object are not
     * populated. Child and child list properties are not populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T findLean(Query<T> query) throws UnifyException;

    /**
     * Finds constraining record that may prevent supplied record from being
     * successfully created.
     * 
     * @param record
     * @return contstraining record if found otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    <T extends Entity> T findConstraint(T record) throws UnifyException;

    /**
     * Finds record by query. Does not fetch attribute lists.
     * 
     * @param query
     *            the query
     * @return the list of record found
     * @throws UnifyException
     *             if an error occurs during search
     */
    <T extends Entity> List<T> findAll(Query<T> query) throws UnifyException;

    /**
     * Fetches record by query as a map. Uses query key as result map key. Does not
     * fetch attribute lists.
     * 
     * @param keyClass
     *            the map key class
     * @param keyName
     *            the key field name
     * @param query
     *            the query
     * @return the result map
     * @throws UnifyException
     *             if an error occurs
     */
    <T, U extends Entity> Map<T, U> findAllMap(Class<T> keyClass, String keyName, Query<U> query) throws UnifyException;

    /**
     * Returns a map of lists by key.
     * 
     * @param keyClass
     *            the map key class
     * @param keyName
     *            the key name
     * @param query
     *            the search query
     * @return map of lists
     * @throws UnifyException
     *             if an error occurs
     */
    <T, U extends Entity> Map<T, List<U>> findAllListMap(Class<T> keyClass, String keyName, Query<U> query)
            throws UnifyException;

    /**
     * Retrieves a record by id from associated view. List-only properties of
     * returned object are populated. Child and child list properties are populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @throws UnifyException
     *             if record is not found
     */
    <T extends Entity> T list(Class<T> clazz, Object id) throws UnifyException;

    /**
     * Retrieves a record by id and version number from associated view. List-only
     * properties of returned object are populated. Child and child list properties
     * are populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @param versionNo
     *            the version number
     * @throws UnifyException
     *             if record is not found
     */
    <T extends Entity> T list(Class<T> clazz, Object id, Object versionNo) throws UnifyException;

    /**
     * Retrieves a record by query from associated view. List-only properties of
     * returned object are populated. Child and child list properties are populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. if an error occurs
     */
    <T extends Entity> T list(Query<T> query) throws UnifyException;

    /**
     * Retrieves a record by id from associated view. List-only properties of
     * returned object are populated. Child and child list properties are not
     * populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @throws UnifyException
     *             if record is not found
     */
    <T extends Entity> T listLean(Class<T> clazz, Object id) throws UnifyException;

    /**
     * Retrieves a record by id and version number from associated view. List-only
     * properties of returned object are populated. Child and child list properties
     * are not populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @param versionNo
     *            the version number
     * @throws UnifyException
     *             if record is not found
     */
    <T extends Entity> T listLean(Class<T> clazz, Object id, Object versionNo) throws UnifyException;

    /**
     * Retrieves a record by query from associated view. List-only properties of
     * returned object are populated. Child and child list properties are not
     * populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. if an error occurs
     */
    <T extends Entity> T listLean(Query<T> query) throws UnifyException;

    /**
     * Retrieves list of record by query from associated view. Does not fetch
     * attribute lists.
     * 
     * @param query
     *            the query
     * @return the list of record found
     * @throws UnifyException
     *             -if an error occurs during search
     */
    <T extends Entity> List<T> listAll(Query<T> query) throws UnifyException;

    /**
     * Fetches map of records. Uses query key as result map key. Does not fetch
     * attribute lists.
     * 
     * @param keyClass
     *            the map key class
     * @param keyName
     *            the key field name
     * @param query
     *            the query
     * @return the result map
     * @throws UnifyException
     *             if an error occurs
     */
    <T, U extends Entity> Map<T, U> listAll(Class<T> keyClass, String keyName, Query<U> query) throws UnifyException;

    /**
     * Returns a map of lists by key.
     * 
     * @param keyClass
     *            the map key class
     * @param keyName
     *            the key name
     * @param query
     *            the search query
     * @return map of lists
     * @throws UnifyException
     *             if an error occurs
     */
    <T, U extends Entity> Map<T, List<U>> listAllListMap(Class<T> keyClass, String keyName, Query<U> query)
            throws UnifyException;

    /**
     * Lists values of selected field for all record by query. Field must be
     * selected in query.
     * 
     * @param fieldClass
     *            the value class
     * @param fieldName
     *            the value field name
     * @param query
     *            the query
     * @return the list of values found
     * @throws UnifyException
     *             if an error occurs during search
     */
    <T, U extends Entity> List<T> valueList(Class<T> fieldClass, String fieldName, Query<U> query)
            throws UnifyException;

    /**
     * Obtains a set of values of selected field for all records by query. Field
     * must be selected in query.
     * 
     * @param fieldClass
     *            the value class
     * @param fieldName
     *            the value field name
     * @param query
     *            the query
     * @return the set of values found
     * @throws UnifyException
     *             if an error occurs during search
     */
    <T, U extends Entity> Set<T> valueSet(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException;

    /**
     * Obtains a key value map
     * 
     * @param keyClass
     *            the key type
     * @param keyName
     *            the key field name
     * @param valueClass
     *            the value type
     * @param valueName
     *            the value field name
     * @param query
     *            the query
     * @return a map of key/values
     * @throws UnifyException
     *             if an error occurs
     */
    <T, U, V extends Entity> Map<T, U> valueMap(Class<T> keyClass, String keyName, Class<U> valueClass,
            String valueName, Query<V> query) throws UnifyException;

    /**
     * Obtains a key value list map
     * 
     * @param keyClass
     *            the key type
     * @param keyName
     *            the key field name
     * @param valueClass
     *            the value type
     * @param valueName
     *            the value field name
     * @param query
     *            the query
     * @return a map of key/ list values
     * @throws UnifyException
     *             if an error occurs
     */
    <T, U, V extends Entity> Map<T, List<U>> valueListMap(Class<T> keyClass, String keyName, Class<U> valueClass,
            String valueName, Query<V> query) throws UnifyException;

    /**
     * Gets value of selected field for record fetched by query.
     * 
     * @param fieldClass
     *            the value class
     * @param fieldName
     *            the value field name
     * @param query
     *            the query
     * @return the value
     * @throws UnifyException
     *             if no field or multiple fields are selected in criteria. If
     *             multiple or no record match criteria. If an error occurs
     */
    <T, U extends Entity> T value(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException;

    /**
     * Gets minimum value of selected field for record fetched by query.
     * 
     * @param fieldClass
     *            the value class
     * @param fieldName
     *            the value field name
     * @param query
     *            the query
     * @return the minimum value
     * @throws UnifyException
     *             if no field or multiple fields are selected in criteria. If
     *             multiple or no record match criteria. If an error occurs
     */
    <T, U extends Entity> T min(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException;

    /**
     * Gets maximum value of selected field for record fetched by query.
     * 
     * @param fieldClass
     *            the value class
     * @param fieldName
     *            the value field name
     * @param query
     *            the query
     * @return the maximum value
     * @throws UnifyException
     *             if no field or multiple fields are selected in criteria. If
     *             multiple or no record match criteria. If an error occurs
     */
    <T, U extends Entity> T max(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException;

    /**
     * Populate list-only fields of supplied record.
     * 
     * @param record
     *            the record to populate
     * @throws UnifyException
     *             if an error occurs
     */
    void populateListOnly(Entity record) throws UnifyException;

    /**
     * Updates a record by ID.
     * 
     * @param record
     *            the record to modify
     * @return the number of record updated. Always 1.
     * @throws UnifyException
     *             if an error occurs during modify
     */
    int updateById(Entity record) throws UnifyException;

    /**
     * Updates a record by ID and version number.
     * 
     * @param record
     *            the record to modify
     * @return the number of record updated. Always 1.
     * @throws UnifyException
     *             if an error occurs during modify
     */
    int updateByIdVersion(Entity record) throws UnifyException;

    /**
     * Updates record in database by ID. Child records, if any, are not updated.
     * 
     * @param record
     *            the record to update
     * @return the number of record updated. Always 1.
     * @throws UnifyException
     *             if record with ID is not found. If an error occurs
     */
    int updateLeanById(Entity record) throws UnifyException;

    /**
     * Updates record in database by ID and version number. Child records, if any,
     * are not updated.
     * 
     * @param record
     *            the record to update
     * @return the number of record updated.
     * @throws UnifyException
     *             If an error occurs
     */
    int updateLeanByIdVersion(Entity record) throws UnifyException;

    /**
     * Updates a a record by ID
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @param update
     *            the update list
     * @return number of records updated
     * @throws UnifyException
     *             if an error occurs
     */
    int updateById(Class<? extends Entity> clazz, Object id, Update update) throws UnifyException;

    /**
     * Updates record that match results of specified query.
     * 
     * @param query
     *            the query
     * @param update
     *            the update object
     * @return the number of record modified
     * @throws UnifyException
     *             if an error occurs during modify
     */
    int updateAll(Query<? extends Entity> query, Update update) throws UnifyException;

    /**
     * Deletes a record by ID.
     * 
     * @param record
     *            the record to modify
     * @return the number of record deleted. Always 1.
     * @throws UnifyException
     *             if an error occurs during modify
     */
    int deleteById(Entity record) throws UnifyException;

    /**
     * Deletes a record by ID and version number.
     * 
     * @param record
     *            the record to modify
     * @return the number of record deleted. Always 1.
     * @throws UnifyException
     *             if an error occurs during modify
     */
    int deleteByIdVersion(Entity record) throws UnifyException;

    /**
     * Deletes a record with ID.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @return the number of record deleted. Always 1.
     * @throws UnifyException
     *             if an error occurs during delete
     */
    int delete(Class<? extends Entity> clazz, Object id) throws UnifyException;

    /**
     * Deletes record by query.
     * 
     * @param query
     *            the query
     * @return the number of record deleted.
     * @throws UnifyException
     *             if an error occurs during delete
     */
    int deleteAll(Query<? extends Entity> query) throws UnifyException;

    /**
     * Counts number of record that match specified query.
     * 
     * @param query
     *            the query object
     * @return the number of record counted
     * @throws UnifyException
     *             if an error occurs during modify
     */
    int count(Query<? extends Entity> query) throws UnifyException;

    /**
     * Executes an aggregate function for single selected field of records that
     * match specified query.
     * 
     * @param aggregateFunction
     *            the aggregate function
     * @param query
     *            the query to use
     * @return the aggregate object
     * @throws UnifyException
     *             If aggregate function field is unknown for entity. If aggregate
     *             function field is not numeric. If an error occurs
     */
    Aggregation aggregate(AggregateFunction aggregateFunction, Query<? extends Entity> query) throws UnifyException;

    /**
     * Executes an aggregate function (individually) for selected properties of
     * record that match specified criteria.
     * 
     * @param aggregate
     *            the aggregate definition
     * @param query
     *            the aggregated items query
     * @return list of aggregate result
     * @throws UnifyException
     *             if selected fields are not numeric. If no field is selected. If
     *             an error occurs
     */
    List<Aggregation> aggregateMany(Aggregate aggregate, Query<? extends Entity> query) throws UnifyException;

    /**
     * Executes a grouping aggregate function (individually) for selected properties
     * of record that match specified criteria.
     * 
     * @param aggregate
     *            the aggregate definition
     * @param query
     *            the aggregated items query. Must include group-by fields
     * @return list of aggregate result
     * @throws UnifyException
     *             if selected fields are not numeric. If no field is selected. If
     *             an error occurs
     */
    List<GroupAggregation> aggregateGroupMany(Aggregate aggregate, Query<? extends Entity> query) throws UnifyException;

    /**
     * Gets a new instance of an entity extension type.
     * 
     * @param entityClass
     *            the extended entity type
     * @return a new instance of extension type if entity is extended otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    Entity getNewExtensionInstance(Class<? extends Entity> entityClass) throws UnifyException;

    /**
     * Gets the current timestamp in UTC of data source based on session time zone.
     * 
     * @return the UTC timestamp
     * @throws UnifyException
     *             if an error occurs
     */
    Date getNow() throws UnifyException;

    /**
     * Executes callable procedure with no results.
     * 
     * @param callableProc
     *            the callable procedure object.
     * @throws UnifyException
     *             if an error occurs.
     */
    void executeCallable(CallableProc callableProc) throws UnifyException;

    /**
     * Executes callable procedure with result lists.
     * 
     * @param callableProc
     * @return list of result items
     * @throws UnifyException
     *             if an error occurs
     */
    Map<Class<?>, List<?>> executeCallableWithResults(CallableProc callableProc) throws UnifyException;

    /**
     * Creates and sets a new save point on the session save point stack.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void setSavepoint() throws UnifyException;

    /**
     * Clears the last save point from the session save point stack
     * 
     * @throws UnifyException
     *             if no save point exists in the save point stack. If an error
     *             occurs.
     */
    void clearSavepoint() throws UnifyException;

    /**
     * Roll back session and clears all save points.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void rollback() throws UnifyException;

    /**
     * Roll back session to last save point.
     * 
     * @throws UnifyException
     *             if no save point exists in the save point stack. If an error
     *             occurs.
     */
    void rollbackToSavepoint() throws UnifyException;

    /**
     * Commits session transactions and clears all save points.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void commit() throws UnifyException;

    /**
     * Closes session.
     * 
     * @throws UnifyException
     */
    void close() throws UnifyException;

    /**
     * Returns the session's closed state.
     * 
     * @throws UnifyException
     *             - If an error occurs
     */
    boolean isClosed() throws UnifyException;

    /**
     * Returns the session's data source name.
     */
    String getDataSourceName();
}
