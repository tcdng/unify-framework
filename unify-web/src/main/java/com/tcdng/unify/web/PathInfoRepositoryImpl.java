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

    private FactoryMap<String, PathParts> pathParts;

    public PathInfoRepositoryImpl() {
        pagePathInfos = new FactoryMap<String, PagePathInfo>() {

            @Override
            protected PagePathInfo create(String pathId, Object... params) throws Exception {
                return new PagePathInfo(pathId, null, pathId + "/openPage", pathId + "/savePage", pathId + "/closePage",
                        false);
            }
        };

        pathParts = new FactoryMap<String, PathParts>() {

            @Override
            protected PathParts create(String path, Object... params) throws Exception {
                int colIndex = path.lastIndexOf(':');
                String pathId = path;
                String controllerName = path;
                String actionName = null;
                boolean variablePath = false;
                if (colIndex >= 0) {
                    controllerName = path.substring(0, colIndex);
                    int actionPartIndex = path.lastIndexOf('/');
                    if (actionPartIndex > colIndex) {
                        pathId = path.substring(0, actionPartIndex);
                        actionName = path.substring(actionPartIndex);
                    }

                    variablePath = true;
                } else if (getComponentConfig(Controller.class, path) == null) {
                    int actionPartIndex = path.lastIndexOf('/');
                    if (actionPartIndex > 0) {
                        controllerName = path.substring(0, actionPartIndex);
                        pathId = controllerName;
                        actionName = path.substring(actionPartIndex);
                    }
                }

                boolean uiController = false;
                UnifyComponentConfig ucc = getComponentConfig(Controller.class, controllerName);
                if (ucc != null) {
                    uiController = UIController.class.isAssignableFrom(ucc.getType());
                }

                return new PathParts(path, pathId, controllerName, actionName, uiController, variablePath);
            }

            @Override
            protected boolean keep(PathParts pathParts) throws Exception {
                return !pathParts.isVariablePath();
            }

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
    public PathParts getPathParts(Page page) throws UnifyException {
        return pathParts.get(page.getPathId());
    }

    @Override
    public PathParts getPathParts(String path) throws UnifyException {
        return pathParts.get(path);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
