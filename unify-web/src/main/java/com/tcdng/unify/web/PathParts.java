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

import com.tcdng.unify.core.util.StringUtils;

/**
 * Path parts.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PathParts {

    private String fullPath;

    private String pathId;

    private String controllerName;

    private String actionName;

    private boolean uiController;

    private boolean variablePath;

    public PathParts(String fullPath, String pathId, String controllerName, String actionName, boolean uiController,
            boolean variablePath) {
        this.fullPath = fullPath;
        this.pathId = pathId;
        this.controllerName = controllerName;
        this.actionName = actionName;
        this.uiController = uiController;
        this.variablePath = variablePath;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getPathId() {
        return pathId;
    }

    public String getControllerName() {
        return controllerName;
    }

    public String getActionName() {
        return actionName;
    }

    public boolean isUiController() {
        return uiController;
    }

    public boolean isVariablePath() {
        return variablePath;
    }

    public boolean isActionPath() {
        return !StringUtils.isBlank(actionName);
    }
}
