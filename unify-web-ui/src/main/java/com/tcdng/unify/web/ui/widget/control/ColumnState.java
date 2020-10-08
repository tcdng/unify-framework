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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyException;

/**
 * The state of a table column.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ColumnState {

    /**
     * Indicates the column is sortable.
     * 
     * @throws UnifyException
     *             if an error occurs.
     */
    boolean isSortable() throws UnifyException;

    /**
     * Returns the current order of the column.
     */
    boolean isAscending();

    /**
     * Returns the bean field name associated with the column.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getFieldName() throws UnifyException;
}
