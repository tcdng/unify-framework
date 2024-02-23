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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Default implementation of a controller finder.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_CONTROLLERFINDER)
public class ControllerFinderImpl extends AbstractUnifyComponent implements ControllerFinder {

	@Override
	public Controller findController(ControllerPathParts controllerPathParts) throws UnifyException {
		logDebug("Finding controller for path [{0}]...", controllerPathParts.getControllerPath());
		logDebug("Path variable [{0}]...", controllerPathParts.getPathVariable());
		
		final String controllerName = controllerPathParts.getControllerName();
		UnifyComponentConfig unifyComponentConfig = getComponentConfig(Controller.class, controllerName);

		final String path = controllerPathParts.getControllerPath();
		if (unifyComponentConfig == null) {
			// May be a class-loader resource or a real path request
			final String cpath = path.startsWith("/") ? path.substring(1) : path;
			if (IOUtils.isClassLoaderResource(cpath)) {
				ResourceController classLoaderController = (ResourceController) getComponent(
						WebApplicationComponents.APPLICATION_CLASSLOADERRESOURCECONTROLLER);
				classLoaderController.setResourceName(cpath);
				return classLoaderController;
			}

			if (IOUtils.isRealPathResource(getUnifyComponentContext().getWorkingPath(), path)) {
				ResourceController realPathController = (ResourceController) getComponent(
						WebApplicationComponents.APPLICATION_REALPATHRESOURCECONTROLLER);
				realPathController.setResourceName(path);
				return realPathController;
			}
		}

		if (unifyComponentConfig == null) {
			throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_UNKNOWN_COMP, path);
		}

		Controller controller = (Controller) getComponent(controllerName);
		controller.ensureContextResources(controllerPathParts);
		logDebug("Controller with name [{0}] found", controllerName);
		return controller;
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

}
