/*
 * Copyright 2018-2023 The Code Department.
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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Aggregate;
import com.tcdng.unify.core.criterion.AggregateFunction;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.data.Aggregation;
import com.tcdng.unify.core.data.GroupAggregation;

/**
 * Interface that represents a database.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface Database extends UnifyComponent {

	/**
	 * Checks if database is read-only
	 * 
	 * @return true if database is read-only otherwise false
     * @throws UnifyException
     *             if an error occurs
	 */
	boolean isReadOnly() throws UnifyException;

    /**
     * Gets the database dataSource.
     * 
     * @return the dataSource
     * @throws UnifyException
     *             if an error occurs
     */
    DataSource getDataSource() throws UnifyException;

    /**
     * Returns the database manage data source name.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getDataSourceName() throws UnifyException;

    /**
     * Explicitly join current transaction in application database transaction
     * manager.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void joinTransaction() throws UnifyException;

    /**
     * Creates a new database session.
     * 
     * @return the new database session
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    DatabaseSession createDatabaseSession() throws UnifyException;

    /**
     * Finds record of specified type by id. List-only properties of returned object
     * are not populated. Child and child list properties are populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record unique ID
     * @return the record with supplied ID
     * @throws UnifyException
     *             if record with id is not found
     */
    <T extends Entity> T find(Class<T> clazz, Object id) throws UnifyException;

    /**
     * Finds record of specified type by id and version number. List-only properties
     * of returned object are not populated. Child and child list properties are
     * populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record unique ID
     * @param versionNo
     *            the version number
     * @return the record with supplied ID and version number
     * @throws UnifyException
     *             if record with id and version is not found
     */
    <T extends Entity> T find(Class<T> clazz, Object id, Object versionNo) throws UnifyException;

    /**
     * Finds a record by criteria. List-only properties of returned object are not
     * populated. Child and child list properties are populated.
     * 
     * @param query
     *            the query
     * @return matched record
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T find(Query<T> query) throws UnifyException;

    /**
     * Retrieves first record matched by criteria. List-only properties of returned object are not
     * populated. Child and child list properties are populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T findFirst(Query<T> query) throws UnifyException;

    /**
     * Retrieves last record matched by criteria. List-only properties of returned object are not
     * populated. Child and child list properties are populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T findLast(Query<T> query) throws UnifyException;

    /**
     * Finds record of specified type by id. List-only properties of returned object
     * are not populated. Child and child list properties are not populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record unique ID
     * @return the record with supplied ID
     * @throws UnifyException
     *             if record with id is not found
     */
    <T extends Entity> T findLean(Class<T> clazz, Object id) throws UnifyException;

    /**
     * Finds record of specified type by id and version number. List-only properties
     * of returned object are not populated. Child and child list properties are not
     * populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record unique ID
     * @param versionNo
     *            the version number
     * @return the record with supplied ID and version number
     * @throws UnifyException
     *             if record with id and version is not found
     */
    <T extends Entity> T findLean(Class<T> clazz, Object id, Object versionNo) throws UnifyException;

    /**
     * Finds a record by criteria. List-only properties of returned object are not
     * populated. Child and child list properties are not populated.
     * 
     * @param query
     *            the query
     * @return matched record
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T findLean(Query<T> query) throws UnifyException;

    /**
     * Retrieves first record matched by criteria. List-only properties of returned object are not
     * populated. Child and child list properties are not populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T findLeanFirst(Query<T> query) throws UnifyException;

    /**
     * Retrieves last record matched by criteria. List-only properties of returned object are not
     * populated. Child and child list properties are not populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T findLeanLast(Query<T> query) throws UnifyException;

    /**
     * Finds constraining record that may prevent supplied record from being
     * successfully created.
     * 
     * @param record
     * @return constraining record if found otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    <T extends Entity> T findConstraint(T record) throws UnifyException;

    /**
     * Finds all records with fields that match criteria. List-only properties of
     * returned objects are not populated. Child and child list properties are not
     * populated.
     * 
     * @param query
     *            the query
     * @return a list of record that match criteria.
     * @throws UnifyException
     *             if an error occurs
     */
    <T extends Entity> List<T> findAll(Query<T> query) throws UnifyException;

    /**
     * Finds records with their child record by query. Does not fetch list-only
     * fields.
     * 
     * @param query
     *              the query
     * @return the list of records found
     * @throws UnifyException
     *                        if an error occurs during search
     */
    <T extends Entity> List<T> findAllWithChildren(Query<T> query) throws UnifyException;

    /**
     * Finds all records by criteria returning resulting record in a map. The keys
     * of the map are values of fields specified by the key property of the
     * criteria. For instance we have a record class
     * 
     * <pre>
     *     <code>
     *     class Author implements Entity {
     *       String id;
     *       String name;
     *       String description;
     *       ...
     *     }
     *     </code>
     * </pre>
     * 
     * and we want a map by the record property <em>name</em>. We create a criteria
     * object and the keyClass parameter set to String.class (the type of
     * <em>name</em> property)
     * 
     * <pre>
     *     <code>
     *     {@code Query<Author> criteria = ...
     *     Database pm = ...
     *     Map<String, Author> resultMap = pm.findRecord(String.class, "name", criteria);}
     *     </code>
     * </pre>
     * 
     * List-only properties of returned objects are not populated. Child and child
     * list properties are not populated.
     * 
     * @param keyClass
     *            the map key class
     * @param keyName
     *            the key field
     * @param query
     *            the query object.
     * @return the resulting map.
     * @throws UnifyException
     *             if criteria key property is not set. If an error occurs
     */
    <T, U extends Entity> Map<T, U> findAllMap(Class<T> keyClass, String keyName, Query<U> query) throws UnifyException;

    /**
     * Returns a map of lists by key. List-only properties of returned objects are
     * not populated. Child and child list properties are not populated.
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
     * Finds child records into supplied record.
     * 
     * @param record
     *               the record to find children into
     * @throws UnifyException
     *                        if an error occurs
     */
    <T extends Entity> void findChildren(T record) throws UnifyException;

    /**
     * Finds editable child records into supplied record.
     * 
     * @param record
     *               the record to find children into
     * @throws UnifyException
     *                        if an error occurs
     */
    <T extends Entity> void findEditableChildren(T record) throws UnifyException;

    /**
     * Finds read-only child records into supplied record.
     * 
     * @param record
     *               the record to find children into
     * @throws UnifyException
     *                        if an error occurs
     */
    <T extends Entity> void findReadOnlyChildren(T record) throws UnifyException;

    /**
     * Lists record of specified type by id. List-only properties of returned object
     * are populated. Child and child list properties are populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record unique ID
     * @throws UnifyException
     *             if record with id is not found
     */
    <T extends Entity> T list(Class<T> clazz, Object id) throws UnifyException;

    /**
     * Lists record of specified type by id and version number. List-only properties
     * of returned object are populated. Child and child list properties are
     * populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record unique ID
     * @param versionNo
     *            the version number
     * @throws UnifyException
     *             if record with id and version is not found
     */
    <T extends Entity> T list(Class<T> clazz, Object id, Object versionNo) throws UnifyException;

    /**
     * Lists record by criteria. List-only properties of returned object are
     * populated. Child and child list properties are populated.
     * 
     * @param query
     *            the query
     * @return record if found otherwise null. List-only properties (if any) of
     *         returned object are populated.
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T list(Query<T> query) throws UnifyException;

    /**
     * Retrieves first record matched by criteria from associated view. List-only properties of
     * returned object are populated. Child and child list properties are populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T listFirst(Query<T> query) throws UnifyException;

    /**
     * Retrieves last record matched by criteria from associated view. List-only properties of
     * returned object are populated. Child and child list properties are populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T listLast(Query<T> query) throws UnifyException;

    /**
     * Lists record of specified type by id. List-only properties of returned object
     * are populated. Child and child list properties are not populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record unique ID
     * @throws UnifyException
     *             if record with id is not found
     */
    <T extends Entity> T listLean(Class<T> clazz, Object id) throws UnifyException;

    /**
     * Lists record of specified type by id and version number. List-only properties
     * of returned object are populated. Child and child list properties are not
     * populated.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record unique ID
     * @param versionNo
     *            the version number
     * @throws UnifyException
     *             if record with id and version is not found
     */
    <T extends Entity> T listLean(Class<T> clazz, Object id, Object versionNo) throws UnifyException;

    /**
     * Lists record by criteria. List-only properties of returned object are
     * populated. Child and child list properties are not populated.
     * 
     * @param query
     *            the query
     * @return record if found otherwise null. List-only properties (if any) of
     *         returned object are populated.
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T listLean(Query<T> query) throws UnifyException;

    /**
     * Retrieves first record matched by criteria from associated view. List-only properties of
     * returned object are populated. Child and child list properties are not populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T listLeanFirst(Query<T> query) throws UnifyException;

    /**
     * Retrieves last record matched by criteria from associated view. List-only properties of
     * returned object are populated. Child and child list properties are not populated.
     * 
     * @param query
     *            the query
     * @return the record found otherwise null
     * @throws UnifyException
     *             if multiple records are found. If an error occurs
     */
    <T extends Entity> T listLeanLast(Query<T> query) throws UnifyException;

    /**
     * Lists all records with fields that match criteria. List-only properties of
     * returned records are populated. Child and child list properties are not
     * populated.
     * 
     * @param query
     *            the query
     * @throws UnifyException
     *             if an error occurs
     */
    <T extends Entity> List<T> listAll(Query<T> query) throws UnifyException;

    /**
     * Retrieves list of record with children by query from associated view.
     * 
     * @param query
     *              the query
     * @return the list of records found
     * @throws UnifyException
     *                        -if an error occurs during search
     */
    <T extends Entity> List<T> listAllWithChildren(Query<T> query) throws UnifyException;

    /**
     * Lists all records with fields that match criteria returning resulting record
     * in a map. List-only properties of returned records are populated. Child and
     * child list properties are not populated.
     * 
     * @param keyClass
     *            the map key class should be the same as the criteria key field
     *            type
     * @param keyName
     *            the key field
     * @param query
     *            the query object.
     * @throws UnifyException
     *             if an error occurs
     */
    <T, U extends Entity> Map<T, U> listAllMap(Class<T> keyClass, String keyName, Query<U> query) throws UnifyException;

    /**
     * Returns a map of lists by key. List-only properties of returned records are
     * populated. Child and child list properties are not populated.
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
     * Lists child records into supplied record.
     * 
     * @param record
     *               the record to list children into
     * @throws UnifyException
     *                        if an error occurs
     */
    <T extends Entity> void listChildren(T record) throws UnifyException;

    /**
     * Lists editable child records into supplied record.
     * 
     * @param record
     *               the record to list children into
     * @throws UnifyException
     *                        if an error occurs
     */
    <T extends Entity> void listEditableChildren(T record) throws UnifyException;


    /**
     * Lists read-only child records into supplied record.
     * 
     * @param record
     *               the record to list children into
     * @throws UnifyException
     *                        if an error occurs
     */
    <T extends Entity> void listReadOnlyChildren(T record) throws UnifyException;

    /**
     * Returns a list of values of a particular field for all record that match
     * supplied criteria. The field, which can be a list-only field, must be
     * selected in the criteria object. For instance we have a record class
     * 
     * <pre>
     *     <code>
     *     class Fruit implements Entity {
     *       Long id;
     *       String name;
     *       double price;
     *       ...
     *     }
     *     </code>
     * </pre>
     * 
     * and we want a list of names for all fruits with price &gt; 10.0. We create a
     * criteria object and set "price" greater that 10.0 and select the "name"
     * field. Then we invoke this method with the criteria and the fieldClass
     * parameter set to String.class (the type of <em>name</em> property)
     * 
     * <pre>
     *     <code>
     *     {@code Query&lt;Fruit&gt; criteria = ...}
     *     criteria.greater("price", 10.0);
     *     criteria.select("name");
     *     Database pm = ...
     *     List&lt;String&gt; names = pm.listFieldValues(String.class, criteria);
     *     </code>
     * </pre>
     * 
     * @param fieldClass
     *            the field type
     * @param fieldName
     *            the field name
     * @param query
     *            the query with field selected
     * @throws UnifyException
     *             if no field or multiple fields are selected in criteria. If an
     *             error occurs
     */
    <T, U extends Entity> List<T> valueList(Class<T> fieldClass, String fieldName, Query<U> query)
            throws UnifyException;

    /**
     * Returns the value of a record's field.
     * 
     * @param fieldClass
     *            the field type
     * @param fieldName
     *            the field name
     * @param query
     *            the query used to identify the record
     * @throws UnifyException
     *             if no field or multiple fields are selected in criteria. If
     *             multiple or no record match criteria. If an error occurs
     */
    <T, U extends Entity> T value(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException;

	/**
	 * Adds supplied value to field for all records that match criteria.
	 * 
	 * @param fieldClass the value type
	 * @param fieldName  the field name
	 * @param val        the value to add
	 * @param query      the criteria
	 * @return the number of records updated
	 * @throws UnifyException if an error occurs
	 */
	<T extends Number, U extends Entity> int add(Class<T> fieldClass, String fieldName, T val, Query<U> query)
			throws UnifyException;

	/**
	 * Subtracts supplied value to field for all records that match criteria.
	 * 
	 * @param fieldClass the value type
	 * @param fieldName  the field name
	 * @param val        the value to add
	 * @param query      the criteria
	 * @return the number of records updated
	 * @throws UnifyException if an error occurs
	 */
	<T extends Number, U extends Entity> int subtract(Class<T> fieldClass, String fieldName, T val, Query<U> query)
			throws UnifyException;

	/**
	 * Multiplies supplied value to field for all records that match criteria.
	 * 
	 * @param fieldClass the value type
	 * @param fieldName  the field name
	 * @param val        the value to add
	 * @param query      the criteria
	 * @return the number of records updated
	 * @throws UnifyException if an error occurs
	 */
	<T extends Number, U extends Entity> int multiply(Class<T> fieldClass, String fieldName, T val, Query<U> query)
			throws UnifyException;

	/**
	 * Divides supplied value to field for all records that match criteria.
	 * 
	 * @param fieldClass the value type
	 * @param fieldName  the field name
	 * @param val        the value to add
	 * @param query      the criteria
	 * @return the number of records updated
	 * @throws UnifyException if an error occurs
	 */
	<T extends Number, U extends Entity> int divide(Class<T> fieldClass, String fieldName, T val, Query<U> query)
			throws UnifyException;

    /**
     * Returns the minimum value of a record field by criteria.
     * 
     * @param fieldClass
     *            the field type
     * @param fieldName
     *            the field name
     * @param query
     *            the query used to identify the record
     * @throws UnifyException
     *             If an error occurs
     */
    <T extends Number, U extends Entity> T min(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException;

    /**
     * Returns the maximum value of a record field by criteria.
     * 
     * @param fieldClass
     *            the field type
     * @param fieldName
     *            the field name
     * @param query
     *            the query used to identify the record
     * @throws UnifyException
     *             If an error occurs
     */
    <T extends Number, U extends Entity> T max(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException;

    /**
     * Returns a set of values of a particular field for all record that match
     * supplied criteria. The field, which can be a list-only field, must be
     * selected in the criteria object.
     * 
     * @param fieldClass
     *            the field type
     * @param fieldName
     *            the field name
     * @param query
     *            the query with field selected
     * @throws UnifyException
     *             if no field or multiple fields are selected in criteria. If an
     *             error occurs
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
     * Populate list-only fields of supplied record.
     * 
     * @param record
     *            the record to populate
     * @throws UnifyException
     *             if an error occurs
     */
    void populateListOnly(Entity record) throws UnifyException;

    /**
     * Creates a new record in database.
     * 
     * @param record
     *            the record to persist
     * @return the unique ID of the newly persisted record
     * @throws UnifyException
     *             if an error occurs
     */
    Object create(Entity record) throws UnifyException;

    /**
     * Updates record in database by ID. Child records, if any, are updated.
     * 
     * @param record
     *            the record to update
     * @return the number of record updated. Always 1.
     * @throws UnifyException
     *             if record with ID is not found. If an error occurs
     */
    int updateById(Entity record) throws UnifyException;

    /**
     * Updates record in database by ID and version number. Child records, if any,
     * are updated.
     * 
     * @param record
     *            the record to update
     * @return the number of record updated.
     * @throws UnifyException
     *             If an error occurs
     */
    int updateByIdVersion(Entity record) throws UnifyException;

    /**
     * Updates a record by ID. Only editable children are updated.
     * 
     * @param record
     *            the record to modify
     * @return the number of record updated. Always 1.
     * @throws UnifyException
     *             if an error occurs during modify
     */
    int updateByIdEditableChildren(Entity record) throws UnifyException;

    /**
     * Updates a record by ID and version number. Only editable children are updated.
     * 
     * @param record
     *            the record to modify
     * @return the number of record updated. Always 1.
     * @throws UnifyException
     *             if an error occurs during modify
     */
    int updateByIdVersionEditableChildren(Entity record) throws UnifyException;

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
     * Updates record by ID.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record ID
     * @param update
     *            update object
     * @return number of records updated
     * @throws UnifyException
     *             if an error occurs
     */
    int updateById(Class<? extends Entity> clazz, Object id, Update update) throws UnifyException;

    /**
     * Updates all records with fields that match criteria using supplied update
     * information.
     * 
     * @param query
     *            the query object
     * @param update
     *            the update information object
     * @return the number of record updated
     * @throws UnifyException
     *             if an error occurs
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
     * Deletes a record by ID.
     * 
     * @param clazz
     *            the record type
     * @param id
     *            the record to delete
     * @return the number of record deleted. Always 1.
     * @throws UnifyException
     *             if record with ID is not found. If an error occurs
     */
    int delete(Class<? extends Entity> clazz, Object id) throws UnifyException;

    /**
     * Deletes all records with fields that match criteria.
     * 
     * @param query
     *            the query object
     * @return the number of record deleted
     * @throws UnifyException
     *             if an error occurs
     */
    int deleteAll(Query<? extends Entity> query) throws UnifyException;

    /**
     * Counts all records with fields that match criteria.
     * 
     * @param query
     *            the query object
     * @return the record count
     * @throws UnifyException
     *             if an error occurs
     */
    <T extends Entity> int countAll(Query<T> query) throws UnifyException;

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
     *            the aggregated items query.
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
     * @return a new instance of extension type
     * @throws UnifyException
     *             if an error occurs
     */
    Entity getExtendedInstance(Class<? extends Entity> entityClass) throws UnifyException;

    /**
     * Gets the current UTC timestamp of database based on session time zone.
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
}
