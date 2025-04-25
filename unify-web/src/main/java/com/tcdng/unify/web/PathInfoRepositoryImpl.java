/*
 * Copyright 2018-2025 The Code Department.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @since 4.1
 */
@Component(WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY)
public class PathInfoRepositoryImpl extends AbstractUnifyComponent implements PathInfoRepository {

	private FactoryMap<String, ControllerPathParts> controllerPathParts;

	private static Map<String, String> operations;
	static {
		Map<String, String> map = new HashMap<String, String>();
		map.put("/count", "count");
		operations = Collections.unmodifiableMap(map);
	}

	public PathInfoRepositoryImpl() {
		controllerPathParts = new FactoryMap<String, ControllerPathParts>() {

			@Override
			protected ControllerPathParts create(final String controllerPath, Object... params) throws Exception {
				UnifyComponentConfig ucc = null;
				String pathId = controllerPath;
				String controllerName = controllerPath;
				String actionName = null;
				String operation = null;
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
						if (StringUtils.isResourceIdPath(actionName)) {
							resourceId = Long.parseLong(actionName.substring(1));
						} else {
							operation = operations.get(actionName);
						}

						if (resourceId != null || operation != null) {
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

				DocPathParts docPathParts = null;
				final String[] _docParts = controllerPath.split("/", 3);
				if (_docParts.length >= 2) {
					final String docControllerName = "/" + _docParts[1];
					String docPath = _docParts.length == 3 ? docPath = _docParts[2] : null;
					String section = null;
					if (docPath != null) {
						int index = docPath.lastIndexOf('#');
						if (index > 0) {
							section = docPath.substring(index + 1);
							docPath = docPath.substring(0, index);
						}
					}

					docPathParts = new DocPathParts(docControllerName, docPath, section);
				}

				boolean sessionless = ucc == null ? false : SessionlessController.class.isAssignableFrom(ucc.getType());
				return new ControllerPathParts(docPathParts, controllerPath, pathId, controllerName, pathVariables,
						actionName, operation, resourceId, sessionless);
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
