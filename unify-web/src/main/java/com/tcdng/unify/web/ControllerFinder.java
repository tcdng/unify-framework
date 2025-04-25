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

import java.util.Set;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Controller finder component.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ControllerFinder extends UnifyComponent {

	/**
	 * Sets controller aliases.
	 * 
	 * @param controllerName the controller name
	 * @param aliases        the aliases to set
	 * @throws UnifyException if an error occurs
	 */
	void setControllerAliases(String controllerName, Set<String> aliases) throws UnifyException;

	/**
	 * Finds a controller component using supplied path parts..
	 * 
	 * @param controllerPathParts the path parts to use
	 * @throws UnifyException if an error occurs
	 */
	Controller findController(ControllerPathParts controllerPathParts) throws UnifyException;
}
