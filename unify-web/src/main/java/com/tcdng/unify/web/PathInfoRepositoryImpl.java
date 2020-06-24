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

package com.tcdng.unify.web;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.web.ui.Page;

/**
 * Default path information repository implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY)
public class PathInfoRepositoryImpl extends AbstractUnifyComponent implements PathInfoRepository {

	private FactoryMap<String, PagePathInfo> pagePathInfos;

	private FactoryMap<String, ControllerPathParts> controllerPathParts;

	public PathInfoRepositoryImpl() {
		pagePathInfos = new FactoryMap<String, PagePathInfo>() {

			@Override
			protected PagePathInfo create(String controllerPathId, Object... params) throws Exception {
				return new PagePathInfo(controllerPathId, null, controllerPathId + "/openPage",
						controllerPathId + "/savePage", controllerPathId + "/closePage", false);
			}
		};

		controllerPathParts = new FactoryMap<String, ControllerPathParts>() {

			@Override
			protected ControllerPathParts create(final String controllerPath, Object... params) throws Exception {
				UnifyComponentConfig ucc = null;
				String pathId = controllerPath;
				String controllerName = controllerPath;
				String actionName = null;
				String pathVariable = null;
				int colIndex = controllerPath.lastIndexOf(':');
				if (colIndex >= 0) {
					controllerName = controllerPath.substring(0, colIndex);
					int actionPartIndex = controllerPath.lastIndexOf('/');
					if (actionPartIndex > colIndex) {
						pathId = controllerPath.substring(0, actionPartIndex);
						pathVariable = controllerPath.substring(colIndex + 1, actionPartIndex);
						actionName = controllerPath.substring(actionPartIndex);
					} else {
						pathVariable = controllerPath.substring(colIndex + 1);
					}
				} else if ((ucc = getComponentConfig(Controller.class, controllerName)) == null) {
					int actionPartIndex = controllerPath.lastIndexOf('/');
					if (actionPartIndex > 0) {
						controllerName = controllerPath.substring(0, actionPartIndex);
						pathId = controllerName;
						actionName = controllerPath.substring(actionPartIndex);
					}
				}

				boolean uiController = false;
				if (ucc == null) {
					ucc = getComponentConfig(Controller.class, controllerName);
				}

				if (ucc != null) {
					uiController = UIController.class.isAssignableFrom(ucc.getType());
				}

				return new ControllerPathParts(controllerPath, pathId, controllerName, pathVariable, actionName,
						uiController);
			}

			// @Override
			// protected boolean keep(ControllerPathParts controllerPathParts) throws
			// Exception {
			// return !controllerPathParts.isVariablePath();
			// }

		};
	}

	@Override
	public PagePathInfo getPagePathInfo(Page page) throws UnifyException {
		return pagePathInfos.get(page.getPathId());
	}

	@Override
	public PagePathInfo getPagePathInfo(String path) throws UnifyException {
		return pagePathInfos.get(path);
	}

	@Override
	public ControllerPathParts getControllerPathParts(Page page) throws UnifyException {
		return controllerPathParts.get(page.getPathId());
	}

	@Override
	public ControllerPathParts getControllerPathParts(String controllerPath) throws UnifyException {
		return controllerPathParts.get(controllerPath);
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}
}
