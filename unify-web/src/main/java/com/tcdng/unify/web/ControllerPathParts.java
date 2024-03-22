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

import java.util.List;

import com.tcdng.unify.core.util.DataUtils;

/**
 * Controller path parts.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ControllerPathParts {

	private String controllerPath;

	private String controllerPathId;

	private String controllerName;

	private List<String> pathVariables;

	private String actionName;

	private boolean sessionless;

	public ControllerPathParts(String controllerPath, String controllerPathId, String controllerName,
			List<String> pathVariables, String actionName, boolean sessionless) {
		this.controllerPath = controllerPath;
		this.controllerPathId = controllerPathId;
		this.controllerName = controllerName;
		this.pathVariables = pathVariables;
		this.actionName = actionName;
		this.sessionless = sessionless;
	}

	public String getControllerPath() {
		return controllerPath;
	}

	public String getControllerPathId() {
		return controllerPathId;
	}

	public String getControllerName() {
		return controllerName;
	}

	public String getActionName() {
		return actionName;
	}

	public List<String> getPathVariables() {
		return pathVariables;
	}

	public boolean isSessionless() {
		return sessionless;
	}

	public boolean isVariablePath() {
		return !DataUtils.isBlank(pathVariables);
	}

	public boolean isActionPath() {
		return actionName != null;
	}
}
