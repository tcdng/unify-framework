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
package com.tcdng.unify.web;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.BindingInfo;
import com.tcdng.unify.web.util.WebUtils;

/**
 * Encapsulates information about a page controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PageControllerInfo extends UserInterfaceControllerInfo {

	private Map<String, Action> actionMap;

	private Map<String, Result> resultMap;

	private Set<String> dynamicPanels;

	public PageControllerInfo(String pageBeanName, Map<String, Action> pageActionMap, Map<String, Result> resultMap,
			Map<String, BindingInfo> pageNamePropertyBindingMap) {
		super(pageBeanName, pageNamePropertyBindingMap);
		this.actionMap = pageActionMap;
		this.resultMap = resultMap;
		this.dynamicPanels = new HashSet<String>();
	}

	public synchronized void addBindings(String dynamicPanelName, Map<String, BindingInfo> pageNamePropertyBindingMap) {
		if (!dynamicPanels.contains(dynamicPanelName)) {
			dynamicPanels.add(dynamicPanelName);
			this.addBindings(pageNamePropertyBindingMap);
		}
	}

	/**
	 * Retrieves all action names associated with the page controller.
	 */
	public Set<String> getActionNames() {
		return this.actionMap.keySet();
	}

	/**
	 * Gets an action by specified name. Action names are full path names composed
	 * of the page controller name, a forward slash and the handler method name.
	 * 
	 * @param name
	 *            the action name
	 * @return the page action
	 * @throws UnifyException
	 *             if page action info with name is unknown
	 */
	public Action getAction(String name) throws UnifyException {
		Action action = this.actionMap.get(name);
		if (action == null) {
			action = this.actionMap.get(WebUtils.extractPathFromBeanIndexedPath(name));
			if (action != null) {
				this.actionMap.put(name, action); // Indexed paths must be limited
			} else {
				throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_UNKNOWN_ACTION, this.getControllerName(),
						name);
			}
		}
		return action;
	}

	/**
	 * Retrieves all result mapping names associated with the page controller.
	 */
	public Set<String> getResultNames() {
		return this.resultMap.keySet();
	}

	/**
	 * Tests of page controller has result mapping with supplied name.
	 * 
	 * @param name
	 *            the name to test with
	 */
	public boolean hasResultWithName(String name) {
		return this.resultMap.containsKey(name);
	}

	/**
	 * Gets a result mapping by specified name.
	 * 
	 * @param name
	 *            the result mapping name
	 * @return the result object
	 * @throws UnifyException
	 *             if result with name is unknown
	 */
	public Result getResult(String name) throws UnifyException {
		Result result = this.resultMap.get(name);
		if (result == null) {
			throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_UNKNOWN_RESULT, this.getControllerName(), name);
		}
		return result;
	}
}
