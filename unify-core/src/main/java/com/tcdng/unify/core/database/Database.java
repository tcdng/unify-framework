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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.Aggregate;
import com.tcdng.unify.core.data.AggregateType;
import com.tcdng.unify.core.operation.Update;

/**
 * Interface that represents a database.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface Database extends UnifyComponent {

	/**
	 * Returns the database transaction manager.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	DatabaseTransactionManager getTransactionManager() throws UnifyException;

	/**
	 * Returns the database manage data source name.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String getDataSourceName() throws UnifyException;

	/**
	 * Finds record of specified type by id.
	 * 
	 * @param clazz
	 *            the record type
	 * @param id
	 *            the record unique ID
	 * @return If found, view-only properties (if any) of returned object are not
	 *         populated.
	 * @throws UnifyException
	 *             if record with id is not found
	 */
	<T extends Entity> T find(Class<T> clazz, Object id) throws UnifyException;

	/**
	 * Finds record of specified type by id and version number.
	 * 
	 * @param clazz
	 *            the record type
	 * @param id
	 *            the record unique ID
	 * @param versionNo
	 *            the version number
	 * @return If found, view-only properties (if any) of returned object are not
	 *         populated.
	 * @throws UnifyException
	 *             if record with id and version is not found
	 */
	<T extends Entity> T find(Class<T> clazz, Object id, Object versionNo) throws UnifyException;

	/**
	 * Finds a record by criteria.
	 * 
	 * @param query
	 *            the query
	 * @return record if found otherwise null. List-only properties (if any) of
	 *         returned object are not populated.
	 * @throws UnifyException
	 *             if multiple records are found. If an error occurs
	 */
	<T extends Entity> T find(Query<T> query) throws UnifyException;

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
	 * Finds all records with fields that match criteria.
	 * 
	 * @param query
	 *            the query
	 * @return a list of record that match criteria. List-only properties (if any)
	 *         of record are not populated.
	 * @throws UnifyException
	 *             if an error occurs
	 */
	<T extends Entity> List<T> findAll(Query<T> query) throws UnifyException;

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
	 *     {@code Query<Author> criteria = ...}
	 *     Database pm = ...
	 *     Map<String, Author> resultMap = pm.findRecord(String.class, "name", criteria);
	 *     </code>
	 * </pre>
	 * 
	 * @param keyClass
	 *            the map key class
	 * @param keyName
	 *            the key field
	 * @param query
	 *            the query object with the {@link Query#key(String)} set.
	 * @return the resulting map. List-only properties (if any) of persistent data
	 *         are not populated.
	 * @throws UnifyException
	 *             if criteria key property is not set. If an error occurs
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
	 * Lists record of specified type by id. Similar to {@link #find(Class, Object)}
	 * but with view-only properties of returned record also populated.
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
	 * Lists record of specified type by id and version number. Similar to
	 * {@link #find(Class, Object, Object)} but with view-only properties of
	 * returned record also populated.
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
	 * Lists record by criteria. Similar to {@link #find(Query)} but with view-only
	 * properties of returned record also populated.
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
	 * Lists all records with fields that match criteria. Similar to
	 * {@link #findAll(Query)} but with view-only properties of returned record also
	 * populated.
	 * 
	 * @param query
	 *            the query
	 * @throws UnifyException
	 *             if an error occurs
	 */
	<T extends Entity> List<T> listAll(Query<T> query) throws UnifyException;

	/**
	 * Lists all records with fields that match criteria returning resulting record
	 * in a map. Similar to {@link #findAll(Class, Query)} but with view-only
	 * properties of returned record also populated.
	 * 
	 * @param keyClass
	 *            the map key class should be the same as the criteria key field
	 *            type
	 * @param keyName
	 *            the key field
	 * @param query
	 *            the query object with the {@link Query#key(String)} set.
	 * @throws UnifyException
	 *             if an error occurs
	 */
	<T, U extends Entity> Map<T, U> listAllMap(Class<T> keyClass, String keyName, Query<U> query) throws UnifyException;

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
	 * Returns a list of values of a particular field for all record that match
	 * supplied criteria. The field, which can be a view-only field, must be
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
	 * and we want a list of names for all fruits with price > 10.0. We create a
	 * criteria object and set "price" greater that 10.0 and select the "name"
	 * field. Then we invoke this method with the criteria and the fieldClass
	 * parameter set to String.class (the type of <em>name</em> property)
	 * 
	 * <pre>
	 *     <code>
	 *     {@code Query<Fruit> criteria = ...}
	 *     criteria.greater("price", 10.0);
	 *     criteria.select("name");
	 *     Database pm = ...
	 *     List<String> names = pm.listFieldValues(String.class, criteria);
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
	 * Returns a set of values of a particular field for all record that match
	 * supplied criteria. The field, which can be a view-only field, must be
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
	 * Updates record in database by ID.
	 * 
	 * @param record
	 *            the record to update
	 * @return the number of record updated. Always 1.
	 * @throws UnifyException
	 *             if record with ID is not found. If an error occurs
	 */
	int updateById(Entity record) throws UnifyException;

	/**
	 * Updates record in database by ID and version number.
	 * 
	 * @param record
	 *            the record to update
	 * @return the number of record updated.
	 * @throws UnifyException
	 *             If an error occurs
	 */
	int updateByIdVersion(Entity record) throws UnifyException;

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
	int countAll(Query<? extends Entity> query) throws UnifyException;

	/**
	 * Executes an aggregate function (individually) for selected properties of
	 * record that match specified criteria.
	 * 
	 * @param aggregateType
	 *            the aggregate type
	 * @param query
	 *            the aggregated items query
	 * @return list of aggregate result
	 * @throws UnifyException
	 *             if selected fields are not numeric. If no field is selected. If
	 *             an error occurs
	 */
	List<Aggregate<?>> aggregate(AggregateType aggregateType, Query<? extends Entity> query) throws UnifyException;

	/**
	 * Gets the current timestamp of database.
	 * 
	 * @return the timestamp
	 * @throws UnifyException
	 *             if an error occurs
	 */
	Date getNow() throws UnifyException;
}
