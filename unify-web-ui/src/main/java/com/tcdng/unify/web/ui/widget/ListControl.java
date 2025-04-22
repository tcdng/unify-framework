/*
 * Copyright 2018-2024 The Code Department.
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

import java.util.List;
import java.util.Map;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;

/**
 * List control interface.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ListControl extends Control {

    /**
     * Gets list control information.
     * 
     * @param formatter
     *                  optional label formatter
     * @return a list control information object
     * @throws UnifyException
     *                        if an error occurs
     */
    ListControlInfo getListControlInfo(Formatter<Object> formatter) throws UnifyException;

    /**
     * Returns listables for a list control.
     * 
     * @return listables
     * @throws UnifyException
     *                        if an error occurs
     */
    List<? extends Listable> getListables() throws UnifyException;

    /**
     * Returns key/description pairs for a list control.
     * 
     * @return the pair map
     * @throws UnifyException
     *                        if an error occurs
     */
    Map<String, Listable> getListMap() throws UnifyException;

    /**
     * Returns this control's list.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getList() throws UnifyException;

    /**
     * Gets the list parameters for this control..
     * 
     * @return array of parameters
     * @throws UnifyException
     *                        if an error occurs
     */
    String[] getListParams() throws UnifyException;

    /**
     * Gets the list parameter type
     * 
     * @return the list parameter type
     * @throws UnifyException
     *                        if an error occurs
     */
    ListParamType getListParamType() throws UnifyException;

    /**
     * Returns this control's list key.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getListKey() throws UnifyException;

    /**
     * Returns this control's list description.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    String getListDescription() throws UnifyException;

    /**
     * Returns true if list control allows multiple select.
     */
    boolean isMultiple();
    
    /**
     * Returns true if list labels should be HTML escaped.
     */
    boolean isHtmlEscape() throws UnifyException;
}
