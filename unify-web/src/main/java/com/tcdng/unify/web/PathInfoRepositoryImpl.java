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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.util.WebPathUtils;

/**
 * Default path information repository implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY)
public class PathInfoRepositoryImpl extends AbstractUnifyComponent implements PathInfoRepository {

	private FactoryMap<String, ControllerPathParts> controllerPathParts;

	public PathInfoRepositoryImpl() {
		controllerPathParts = new FactoryMap<String, ControllerPathParts>() {

			@Override
			protected ControllerPathParts create(final String controllerPath, Object... params) throws Exception {
				UnifyComponentConfig ucc = null;
				String pathId = controllerPath;
				String controllerName = controllerPath;
				String actionName = null;
				Long resourceId = null;
				List<String> pathVariables = Collections.emptyList();
				int colIndex = controllerPath.indexOf(':');
				if (colIndex > 0) {
					controllerName = controllerPath.substring(0, colIndex);
					int actionPartIndex = controllerPath.lastIndexOf('/');
					String pathVariable = null;
					if (actionPartIndex > colIndex) {
						pathId = controllerPath.substring(0, actionPartIndex);
						pathVariable = controllerPath.substring(colIndex + 1, actionPartIndex);
						actionName = controllerPath.substring(actionPartIndex);
						if (StringUtils.isInteger(actionName)) {
							resourceId = Long.parseLong(actionName);
							final String _controllerPath = controllerName;
							actionPartIndex = _controllerPath.lastIndexOf('/');
							pathId = _controllerPath.substring(0, actionPartIndex);
							actionName = _controllerPath.substring(actionPartIndex);
						}
					} else {
						pathVariable = controllerPath.substring(colIndex + 1);
					}

					pathVariables = Arrays.asList(pathVariable.split(":"));
				} else if ((ucc = getComponentConfig(Controller.class, controllerName)) == null) {
					int actionPartIndex = controllerPath.lastIndexOf('/');
					if (actionPartIndex > 0) {
						controllerName = controllerPath.substring(0, actionPartIndex);
						pathId = controllerName;
						actionName = controllerPath.substring(actionPartIndex);
						if (StringUtils.isInteger(actionName)) {
							resourceId = Long.parseLong(actionName);
							final String _controllerPath = controllerName;
							actionPartIndex = _controllerPath.lastIndexOf('/');
							controllerName = _controllerPath.substring(0, actionPartIndex);
							pathId = controllerName;
							actionName = _controllerPath.substring(actionPartIndex);
						}
					}
				}

				if (ucc == null) {
					ucc = getComponentConfig(Controller.class, controllerName);
				}

				boolean sessionless = ucc == null ? false : SessionlessController.class.isAssignableFrom(ucc.getType());
				return new ControllerPathParts(controllerPath, pathId, controllerName, pathVariables, actionName,
						resourceId, sessionless);
			}
		};
	}

	@Override
	public ControllerPathParts getControllerPathParts(final String controllerPath) throws UnifyException {
		final String conformingPath = WebPathUtils.stripOffClientId(controllerPath);
		return controllerPathParts.get(conformingPath);
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}
}
