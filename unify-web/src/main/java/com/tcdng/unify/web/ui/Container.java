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
package com.tcdng.unify.web.ui;

import java.util.List;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.ValueStore;

/**
 * A user interface component that contains other user interface components.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface Container extends DataTransferWidget {

	/**
	 * Sets the container's widget repository.
	 * 
	 * @param widgetRepository
	 *            the repository to set
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void setWidgetRepository(WidgetRepository widgetRepository) throws UnifyException;

	/**
	 * Returns true if container has a widget repository.
	 */
	boolean hasWidgetRepository();

	/**
	 * Returns the long names of all widgets in this container.
	 * 
	 * @return the list of component long names
	 * @throws UnifyException
	 *             if an error occurs
	 */
	Set<String> getWidgetLongNames() throws UnifyException;

	/**
	 * Gets widget by long name.
	 * 
	 * @param longName
	 *            - the component long name
	 * @return Widget - the component
	 * @exception UnifyException
	 *                if component with long name is unknown
	 */
	Widget getWidgetByLongName(String longName) throws UnifyException;

	/**
	 * Returns true if widget with supplied long name exists in this container.
	 * 
	 * @param longName
	 *            the long name to use
	 * @throws UnifyException
	 *             if an error occurs
	 */
	boolean isWidget(String longName) throws UnifyException;

	/**
	 * Gets a widget by short name.
	 * 
	 * @param shortName
	 *            the component short name
	 * @return Widget - the component
	 * @exception UnifyException
	 *                if component with short name is unknown
	 */
	Widget getWidgetByShortName(String shortName) throws UnifyException;

	/**
	 * Returns this containers layout component list.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	List<String> getLayoutWidgetLongNames() throws UnifyException;

	/**
	 * Returns repeat value stores.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	List<ValueStore> getRepeatValueStores() throws UnifyException;

	/**
	 * Returns container layout
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	Layout getLayout() throws UnifyException;

	/**
	 * Returns true if container spacing
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	boolean isSpace() throws UnifyException;

	/**
	 * Returns true if container is repeater
	 */
	boolean isRepeater();

	/**
	 * Returns use-layout-if-present flag.
	 */
	boolean isUseLayoutIfPresent();

	/**
	 * Cascades value store to child components.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void cascadeValueStore() throws UnifyException;
}
