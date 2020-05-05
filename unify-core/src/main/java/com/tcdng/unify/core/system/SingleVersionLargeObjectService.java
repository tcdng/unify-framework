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

package com.tcdng.unify.core.system;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessService;

/**
 * Service for maintaining single version of large objects.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface SingleVersionLargeObjectService extends BusinessService {

    /**
     * Stores a binary large object if supplied version is newer than object's
     * current version.
     * 
     * @param applicationName
     *            the application name
     * @param categoryName
     *            the category name
     * @param objectName
     *            the object name
     * @param blob
     *            the large object
     * @param version
     *            the supplied object version
     * @return true if successfully stored. false if supplied version is not newer
     * @throws UnifyException
     *             if version is less than or equal to zero. If an error occurs
     */
    boolean storeBlob(String applicationName, String categoryName, String objectName, byte[] blob, long version)
            throws UnifyException;

    /**
     * Retrieves binary large object maintained by service.
     * 
     * @param applicationName
     *            the application name
     * @param categoryName
     *            the category name
     * @param objectName
     *            the object name
     * @return the large object if found otherwise null;
     * @throws UnifyException
     */
    byte[] retrieveBlob(String applicationName, String categoryName, String objectName) throws UnifyException;

    /**
     * Retrieves binary large object names.
     * 
     * @param applicationName
     *            the application name
     * @param categoryName
     *            the category name
     * @return list of object names
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> retrieveBlobObjectNames(String applicationName, String categoryName) throws UnifyException;

    /**
     * Gets the current version of binary large object maintained by service.
     * 
     * @param applicationName
     *            the application name
     * @param categoryName
     *            the category name
     * @param objectName
     *            the object name
     * @return the current version if found otherwise zero.
     * @throws UnifyException
     *             if an error occurs
     */
    long getBlobVersion(String applicationName, String categoryName, String objectName) throws UnifyException;

    /**
     * Stores a character large object if supplied version is newer than object's
     * current version.
     * 
     * @param applicationName
     *            the application name
     * @param categoryName
     *            the category name
     * @param objectName
     *            the object name
     * @param clob
     *            the large object
     * @param version
     *            the supplied object version
     * @return true if successfully stored. false if supplied version is not newer
     * @throws UnifyException
     *             if version is less than or equal to zero. If an error occurs
     */
    boolean storeClob(String applicationName, String categoryName, String objectName, String clob, long version)
            throws UnifyException;

    /**
     * Retrieves character large object maintained by service.
     * 
     * @param applicationName
     *            the application name
     * @param categoryName
     *            the category name
     * @param objectName
     *            the object name
     * @return the large object if found otherwise null;
     * @throws UnifyException
     */
    String retrieveClob(String applicationName, String categoryName, String objectName) throws UnifyException;

    /**
     * Retrieves character large object names.
     * 
     * @param applicationName
     *            the application name
     * @param categoryName
     *            the category name
     * @return list of object names
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> retrieveClobObjectNames(String applicationName, String categoryName) throws UnifyException;

    /**
     * Gets the current version of character large object maintained by service.
     * 
     * @param applicationName
     *            the application name
     * @param categoryName
     *            the category name
     * @param objectName
     *            the object name
     * @return the current version if found otherwise zero.
     * @throws UnifyException
     *             if an error occurs
     */
    long getClobVersion(String applicationName, String categoryName, String objectName) throws UnifyException;
}
