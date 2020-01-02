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
package com.tcdng.unify.web.ui;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.DataTransfer;

/**
 * User interface component validation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface PageValidation extends Behavior {

    /**
     * Returns the page validation ID
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getId() throws UnifyException;

    /**
     * Sets the page validation ID.
     * 
     * @param id
     *            the id to set
     */
    void setId(String id);

    /**
     * Performs a user interface validation.
     * 
     * @param targetWidgets
     *            the target widgets
     * @param dataTransfer
     *            the data transfer object
     * @return true if validation was successful
     * @throws UnifyException
     *             if an error occurred
     */
    boolean validate(List<Widget> targetWidgets, DataTransfer dataTransfer) throws UnifyException;
}
