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
package com.tcdng.unify.web;

import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;

/**
 * Controller information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class ControllerInfo<T extends Action> {

	private final String controllerName;

	private final Map<String, T> actionByNameMap;

	public ControllerInfo(String controllerName, Map<String, T> actionByNameMap) {
		this.controllerName = controllerName;
		this.actionByNameMap = actionByNameMap;
	}

	public String getControllerName() {
		return controllerName;
	}

	/**
	 * Retrieves all action names associated with the page controller.
	 */
	public Set<String> getActionNames() {
		return actionByNameMap.keySet();
	}

	/**
	 * Gets an action by specified name. Action names are full path names composed
	 * of the page controller name, a forward slash and the handler method name.
	 * 
	 * @param actionName the full action name
	 * @return the page action
	 * @throws UnifyException if page action info with name is unknown
	 */
	public T getAction(String actionName) throws UnifyException {
		T action = actionByNameMap.get(actionName);
		if (action == null) {
			throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_UNKNOWN_ACTION, getControllerName(), actionName);
		}

		return action;
	}
}
