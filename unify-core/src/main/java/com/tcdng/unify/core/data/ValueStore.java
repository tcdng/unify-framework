/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.core.data;

import java.util.Collection;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;

/**
 * Interface representing a value store.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ValueStore {

    /**
     * Sets the policy for this value store
     * 
     * @param policy
     *               the policy to set
     */
    void setPolicy(ValueStorePolicy policy);
    
    /**
     * Retrieves value from store using supplied name.
     * 
     * @param name
     *             the name of the value to retrieve
     * @throws UnifyException
     *                        if an error occurs
     */
    Object retrieve(String name) throws UnifyException;

    /**
     * Retrieves value from store at a particular storage index using supplied name.
     * 
     * @param storageIndex
     *                     the storage index
     * @param name
     *                     the name of the value to retrieve
     * @throws UnifyException
     *                        if an error occurs
     */
    Object retrieve(int storageIndex, String name) throws UnifyException;

    /**
     * Retrieves converted value from store using supplied name.
     * 
     * @param type
     *             the type to convert to
     * @param name
     *             the name of the value to retrieve
     * @throws UnifyException
     *                        if an error occurs
     */
    <T> T retrieve(Class<T> type, String name) throws UnifyException;

    /**
     * Retrieves converted value from store using supplied name.
     * 
     * @param type
     *                  the type to convert to
     * @param name
     *                  the name of the value to retrieve
     * @param formatter
     *                  the formatter for conversion
     * @throws UnifyException
     *                        if an error occurs
     */
    <T> T retrieve(Class<T> type, String name, Formatter<?> formatter) throws UnifyException;

    /**
     * Retrieves converted value from store at a particular storage index using
     * supplied name.
     * 
     * @param type
     *                     the type to convert to
     * @param storageIndex
     *                     the storage index
     * @param name
     *                     the name of the value to retrieve
     * @throws UnifyException
     *                        if an error occurs
     */
    <T> T retrieve(Class<T> type, int storageIndex, String name) throws UnifyException;

    /**
     * Stores a value using supplied name.
     * 
     * @param name
     *              the name of the value
     * @param value
     *              the value to store
     * @throws UnifyException
     *                        if value with supplied name is unknown. If an error
     *                        occurs
     */
    void store(String name, Object value) throws UnifyException;

    /**
     * Stores a value using supplied name and optional formatter.
     * 
     * @param name
     *                  the name of the value
     * @param value
     *                  the value to store
     * @param formatter
     *                  the optional formatter
     * @throws UnifyException
     *                        if value with supplied name is unknown. If an error
     *                        occurs
     */
    void store(String name, Object value, Formatter<?> formatter) throws UnifyException;

    /**
     * Stores a value at particular storage index using supplied name.
     * 
     * @param storageIndex
     *                     the storage index
     * @param name
     *                     the name of the value
     * @param value
     *                     the value to store
     * @throws UnifyException
     *                        if value with supplied name is unknown. If an error
     *                        occurs
     */
    void store(int storageIndex, String name, Object value) throws UnifyException;

    /**
     * Stores a value at particular storage index using supplied name and optional
     * formatter.
     * 
     * @param storageIndex
     *                     the storage index
     * @param name
     *                     the name of the value
     * @param value
     *                     the value to store
     * @param formatter
     *                     the optional formatter
     * @throws UnifyException
     *                        if value with supplied name is unknown. If an error
     *                        occurs
     */
    void store(int storageIndex, String name, Object value, Formatter<?> formatter) throws UnifyException;

    /**
     * Stores a value using supplied name if the current store value is null.
     * 
     * @param name
     *              the name of the value
     * @param value
     *              the value to store
     * @throws UnifyException
     *                        if value with supplied name is unknown. If an error
     *                        occurs
     */
    void storeOnNull(String name, Object value) throws UnifyException;

    /**
     * Stores a value using supplied name and optional formatter if the current
     * store value is null.
     * 
     * @param name
     *                  the name of the value
     * @param value
     *                  the value to store
     * @param formatter
     *                  the optional formatter
     * @throws UnifyException
     *                        if value with supplied name is unknown. If an error
     *                        occurs
     */
    void storeOnNull(String name, Object value, Formatter<?> formatter) throws UnifyException;

    /**
     * Stores a value at particular storage index using supplied name if the current
     * store value is null.
     * 
     * @param storageIndex
     *                     the storage index
     * @param name
     *                     the name of the value
     * @param value
     *                     the value to store
     * @throws UnifyException
     *                        if value with supplied name is unknown. If an error
     *                        occurs
     */
    void storeOnNull(int storageIndex, String name, Object value) throws UnifyException;

    /**
     * Stores a value at particular storage index using supplied name and optional
     * formatter if the current store value is null.
     * 
     * @param storageIndex
     *                     the storage index
     * @param name
     *                     the name of the value
     * @param value
     *                     the value to store
     * @param formatter
     *                     the optional formatter
     * @throws UnifyException
     *                        if value with supplied name is unknown. If an error
     *                        occurs
     */
    void storeOnNull(int storageIndex, String name, Object value, Formatter<?> formatter) throws UnifyException;

    /**
     * Gets temporary value from store using supplied name.
     * 
     * @param name
     *             the name of the value to read
     * @throws UnifyException
     *                        if an error occurs
     */
    Object getTempValue(String name) throws UnifyException;

    /**
     * Gets converted temporary value from store using supplied name.
     * 
     * @param type
     *             the type to convert to
     * @param name
     *             the name of the value to retrieve
     * @throws UnifyException
     *                        if an error occurs
     */
    <T> T getTempValue(Class<T> type, String name) throws UnifyException;

    /**
     * Sets a temporary value using supplied name.
     * 
     * @param name
     *              the name of the value
     * @param value
     *              the value to store
     * @throws UnifyException
     *                        if value with supplied name is unknown. If an error
     *                        occurs
     */
    void setTempValue(String name, Object value) throws UnifyException;

    /**
     * Returns true if supplied name is a temporary value
     * 
     * @param name
     *             the temporary value name
     */
    boolean isTempValue(String name);

    /**
     * Returns true if value store has a gettable value with supplied name.
     * 
     * @param name
     *             the name of the value
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isGettable(String name) throws UnifyException;

    /**
     * Returns true if value store has a settable value with supplied name.
     * 
     * @param name
     *             the name of the value
     * @throws UnifyException
     *                        if an error occurs
     */
    boolean isSettable(String name) throws UnifyException;

    /**
     * Returns the value store value object.
     */
    Object getValueObject();

    /**
     * Returns the value store data marker.
     */
    String getDataMarker();

    /**
     * Sets the value store data marker.
     * 
     * @param dataMarker
     *                   the data marker to set
     */
    void setDataMarker(String dataMarker);

    /**
     * Returns the value store data index.
     */
    int getDataIndex();

    /**
     * Sets the value store data index.
     * 
     * @param dataIndex
     *                  the data index to set
     */
    void setDataIndex(int dataIndex);

    /**
     * Returns the storage size for this value store
     * 
     * @return the storage size
     */
    int size();

    /**
     * Finds the differences between value store.
     * 
     * @param newSource
     *                  the new source value store
     * @return the audit of differences
     * @throws UnifyException
     *                        if an error occurs
     */
    Audit diff(ValueStore newSource) throws UnifyException;

    /**
     * Finds the differences between value store.
     * 
     * @param newSource
     *                            the new source value store
     * @param inclusionFieldNames
     *                            the fields to include in difference check.
     * @return the audit of differences
     * @throws UnifyException
     *                        if an error occurs
     */
    Audit diff(ValueStore newSource, String... inclusionFieldNames) throws UnifyException;

    /**
     * Finds the differences between value store.
     * 
     * @param newSource
     *                            the new source value store
     * @param inclusionFieldNames
     *                            the fields to include in difference check.
     * @return the audit of differences
     * @throws UnifyException
     *                        if an error occurs
     */
    Audit diff(ValueStore newSource, Collection<String> inclusionFieldNames) throws UnifyException;

    /**
     * Copies supplied value store to this object.
     * 
     * @param source
     *               the source value store
     * @throws UnifyException
     *                        if an error occurs
     */
    void copy(ValueStore source) throws UnifyException;

    /**
     * Copies supplied value store to this object with exclusions.
     * 
     * @param source
     *                            the source value store
     * @param exclusionFieldNames
     *                            the fields to exclude from copy.
     * @throws UnifyException
     *                        if an error occurs
     */
    void copyWithExclusions(ValueStore source, String... exclusionFieldNames) throws UnifyException;

    /**
     * Copies supplied value store to this object with inclusions.
     * 
     * @param source
     *                            the source value store
     * @param inclusionFieldNames
     *                            the fields to include in copy.
     * @throws UnifyException
     *                        if an error occurs
     */
    void copyWithInclusions(ValueStore source, String... inclusionFieldNames) throws UnifyException;

    /**
     * Copies supplied value store to this object with exclusions.
     * 
     * @param source
     *                            the source value store
     * @param exclusionFieldNames
     *                            the fields to exclude from copy.
     * @throws UnifyException
     *                        if an error occurs
     */
    void copyWithExclusions(ValueStore source, Collection<String> exclusionFieldNames) throws UnifyException;

    /**
     * Copies supplied value store to this object with inclusions.
     * 
     * @param source
     *                            the source value store
     * @param inclusionFieldNames
     *                            the fields to include in copy.
     * @throws UnifyException
     *                        if an error occurs
     */
    void copyWithInclusions(ValueStore source, Collection<String> inclusionFieldNames) throws UnifyException;
    
    /**
     * Gets reader for this value store object.
     * 
     * @return the value store reader
     */
    ValueStoreReader getReader();

    /**
     * Gets writer for this value store object.
     * 
     * @return the value store writer
     */
    ValueStoreWriter getWriter();
}
