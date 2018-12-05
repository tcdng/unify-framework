/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.web.ui.panel;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.web.ui.Panel;

/**
 * Represents a table CRUD panel.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface TableCrudPanel<T extends Entity> extends Panel {

	interface Event {
		String CREATE = "CREATE";

		String CREATE_NEXT = "CREATE_NEXT";

		String UPDATE = "UPDATE";

		String DELETE = "DELETE";

		String CANCEL = "CANCEL";
	}

	/**
	 * Returns record at position.
	 * 
	 * @param index
	 *            the record index
	 * @throws UnifyException
	 *             if an error occurs
	 */
	T getRecord(int index) throws UnifyException;

	/**
	 * Sets panel record list.
	 * 
	 * @param recordList
	 *            the list to set.
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void setRecordList(List<T> recordList) throws UnifyException;

	/**
	 * Gets panel record list.
	 * 
	 * @return the record list
	 * @throws UnifyException
	 *             if an error occurs
	 */
	List<T> getRecordList() throws UnifyException;

	/**
	 * Clears panel data.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void clear() throws UnifyException;
}
