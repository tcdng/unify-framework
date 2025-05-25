/*
 * Copyright (c) 2018-2025 The Code Department.
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

/**
 * Request path parts.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class RequestPathParts {

    private ControllerPathParts controllerPathParts;

    private String tenantPath;

    public RequestPathParts(ControllerPathParts controllerPathParts, String tenantPath) {
        this.controllerPathParts = controllerPathParts;
        this.tenantPath = tenantPath;
    }

    public RequestPathParts(ControllerPathParts controllerPathParts) {
        this.controllerPathParts = controllerPathParts;
    }

    public void setControllerPathParts(ControllerPathParts controllerPathParts) {
		this.controllerPathParts = controllerPathParts;
	}

	public ControllerPathParts getControllerPathParts() {
        return controllerPathParts;
    }

    public String getControllerPath() {
        return controllerPathParts.getControllerPath();
    }

    public String getControllerPathId() {
        return controllerPathParts.getControllerPathId();
    }

    public String getControllerName() {
        return controllerPathParts.getControllerName();
    }

    public String getActionName() {
        return controllerPathParts.getActionName();
    }

    public List<String> getPathVariables() {
        return controllerPathParts.getPathVariables();
    }

    public boolean isSessionless() {
        return controllerPathParts.isSessionless();
    }

    public boolean isVariablePath() {
        return controllerPathParts.isVariablePath();
    }

    public boolean isActionPath() {
        return controllerPathParts.isActionPath();
    }

    public String getTenantPath() {
        return tenantPath;
    }

    public boolean isWithTenantPath() {
        return tenantPath != null;
    }

    @Override
    public String toString() {
        return "RequestPathParts [getControllerPath()=" + getControllerPath() + ", getControllerName()="
                + getControllerName() + ", getPathVariable()=" + getPathVariables() + ", getTenantPath()="
                + getTenantPath() + "]";
    }
}
