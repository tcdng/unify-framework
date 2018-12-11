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
package com.tcdng.unify.web.ui;

import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.Listable;

/**
 * List control interface.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ListControl extends Control {

	/**
	 * Returns list control data.
	 * 
	 * @param indexes
	 *            indicates JSON indexes array should be generated
	 * @param keys
	 *            indicates JSON keys array should be generated
	 * @param labels
	 *            indicates JSON labels array should be generated
	 * @throws UnifyException
	 *             if an error occurs
	 */
	ListControlJsonData getListControlJsonData(boolean indexes, boolean keys, boolean labels) throws UnifyException;

	/**
	 * Returns listables for a list control.
	 * 
	 * @return listables
	 * @throws UnifyException
	 *             if an error occurs
	 */
	List<? extends Listable> getListables() throws UnifyException;

	/**
	 * Returns key/description pairs for a list control.
	 * 
	 * @return the pair map
	 * @throws UnifyException
	 *             if an error occurs
	 */
	Map<String, String> getListMap() throws UnifyException;

	/**
	 * Returns this control's list.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String getList() throws UnifyException;

	/**
	 * Gets the list parameters for this control..
	 * 
	 * @return array of parameters
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String[] getListParams() throws UnifyException;

	/**
	 * Gets the list parameter type
	 * 
	 * @return the list parameter type
	 * @throws UnifyException
	 *             if an error occurs
	 */
	ListParamType getListParamType() throws UnifyException;

	/**
	 * Returns this control's list key.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String getListKey() throws UnifyException;

	/**
	 * Returns this control's list description.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String getListDescription() throws UnifyException;

	/**
	 * Returns true if list control allows multiple select.
	 */
	boolean isMultiple();
}
