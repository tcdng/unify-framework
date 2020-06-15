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
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.constant.ReservedPageControllerConstants;
import com.tcdng.unify.web.ui.Page;

/**
 * Default path information repository implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_PATHINFOREPOSITORY)
public class PathInfoRepositoryImpl extends AbstractUnifyComponent implements PathInfoRepository {

    @Configurable
    private TenantPathManager tenantPathManager;

    private FactoryMap<String, PagePathInfo> pagePathInfos;

    private FactoryMap<String, RequestPathParts> requestPathParts;

    private FactoryMap<String, ControllerPathParts> controllerPathParts;

    private boolean isTenantPathEnabled;

    public PathInfoRepositoryImpl() {
        pagePathInfos = new FactoryMap<String, PagePathInfo>() {

            @Override
            protected PagePathInfo create(String controllerPathId, Object... params) throws Exception {
                return new PagePathInfo(controllerPathId, null, controllerPathId + "/openPage",
                        controllerPathId + "/savePage", controllerPathId + "/closePage", false);
            }
        };

        requestPathParts = new FactoryMap<String, RequestPathParts>() {

            @Override
            protected RequestPathParts create(String requestPath, Object... params) throws Exception {
                String controllerPath = requestPath;
                String tenantPath = null;
                if (isTenantPathEnabled) {
                    if (StringUtils.isBlank(requestPath)) {
                        throw new UnifyException(UnifyWebErrorConstants.TENANT_PART_EXPECTED_IN_URL);
                    }

                    int cIndex = requestPath.indexOf('/', 1);
                    if (cIndex > 0) {
                        tenantPath = requestPath.substring(0, cIndex);
                        controllerPath = requestPath.substring(cIndex);
                    } else {
                        tenantPath = requestPath;
                        controllerPath = null;
                    }

                    tenantPathManager.verifyTenantPath(tenantPath);
                }

                if (StringUtils.isBlank(controllerPath)) {
                    controllerPath = getContainerSetting(String.class, UnifyWebPropertyConstants.APPLICATION_HOME,
                            ReservedPageControllerConstants.DEFAULT_APPLICATION_HOME);
                }

                return new RequestPathParts(controllerPathParts.get(controllerPath), tenantPath);
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

//            @Override
//            protected boolean keep(ControllerPathParts controllerPathParts) throws Exception {
//                return !controllerPathParts.isVariablePath();
//            }

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
    public RequestPathParts getRequestPathParts(String requestPath) throws UnifyException {
        return requestPathParts.get(requestPath);
    }

    @Override
    public ControllerPathParts getControllerPathParts(Page page) throws UnifyException {
        return controllerPathParts.get(page.getPathId());
    }

    @Override
    public ControllerPathParts getControllerPathParts(String path) throws UnifyException {
        return controllerPathParts.get(path);
    }

    @Override
    protected void onInitialize() throws UnifyException {
        isTenantPathEnabled =
                getContainerSetting(boolean.class, UnifyWebPropertyConstants.APPLICATION_TENANT_PATH_ENABLED, false);
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
