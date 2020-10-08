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
package com.tcdng.unify.web.ui.widget;

import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.format.Formatter;

/**
 * List control utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ListControlUtils extends UnifyComponent {

    /**
     * Gets supplied list control's data.
     * 
     * @param listControl
     *            the list control
     * @param formatter optional label formatter
     * @return the list control data
     * @throws UnifyException
     *             if an error occurs
     */
    ListControlInfo getListControlInfo(ListControl listControl, Formatter<Object> formatter) throws UnifyException;

    /**
     * Returns listables for a list control.
     * 
     * @param listControl
     *            the supplied list control
     * @return listables
     * @throws UnifyException
     *             if an error occurs
     */
    List<? extends Listable> getListables(ListControl listControl) throws UnifyException;

    /**
     * Returns key/description pairs for a list control.
     * 
     * @param listControl
     *            the supplied list control
     * @return the pair map
     * @throws UnifyException
     *             if an error occurs
     */
    Map<String, String> getListMap(ListControl listControl) throws UnifyException;
}
