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

package com.tcdng.unify.web.ui.widget;

import com.tcdng.unify.core.UnifyException;

/**
 * Used for managing a container tabular layout.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface TabularLayout extends Layout {

    /**
     * Returns the layout cell style.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getCellStyle() throws UnifyException;

    /**
     * Returns the layout cell type.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    TabularCellType getCellType() throws UnifyException;

    /**
     * Returns true if layout cells should be padded otherwise a false is returned.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isCellPadding() throws UnifyException;

    /**
     * Returns layout widths.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String[] getWidths() throws UnifyException;

    /**
     * Returns layout heights.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String[] getHeights() throws UnifyException;
}
