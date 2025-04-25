/*
 * Copyright (c) 2018-2025 The Code Department.
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

package com.tcdng.unify.core.file;

import java.io.InputStream;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Application file resource provider.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface FileResourceProvider extends UnifyComponent {

    /**
     * Opens input stream for a file resource.
     * 
     * @param category
     *                     the resource category
     * @param resourceName
     *                     the resource file name
     * @return the input stream if resource is found otherwise null
     * @throws UnifyException
     *                        if an error occurs
     */
    InputStream openFileResourceInputStream(String category, String resourceName) throws UnifyException;

    /**
     * Read s file resource.
     * 
     * @param category
     *                     the resource category
     * @param resourceName
     *                     the resource file name
     * @return the file resource if found otherwise null
     * @throws UnifyException
     *                        if an error occurs
     */
    byte[] readFileResource(String category, String resourceName) throws UnifyException;
}
