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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.PropertyInfo;

/**
 * Encapsulates information about a page controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PageControllerInfo extends UIControllerInfo {

    private Map<String, Action> actionByNameMap;

    private Map<String, Result> resultNameMap;

    private Set<String> dynamicPanels;

    public PageControllerInfo(String pageBeanName, Map<String, Action> actionByNameMap,
            Map<String, Result> resultByNameMap, Map<String, PropertyInfo> pageNameToPropertyMap) {
        super(pageBeanName, pageNameToPropertyMap);
        this.actionByNameMap = actionByNameMap;
        this.resultNameMap = resultByNameMap;
        dynamicPanels = new HashSet<String>();
    }

    public synchronized void addBindings(String dynamicPanelName,
            Map<String, PropertyInfo> pageNamePropertyBindingMap) {
        if (!dynamicPanels.contains(dynamicPanelName)) {
            dynamicPanels.add(dynamicPanelName);
            addPageNameToPropertyMappings(pageNamePropertyBindingMap);
        }
    }

    /**
     * Retrieves all action names associated with the page controller.
     */
    public Set<String> getActionNames() {
        return actionByNameMap.keySet();
    }

    /**
     * Gets an action by specified name. Action names are full path names composed
     * of the page controller name, a forward slash and the handler method name.
     * 
     * @param actionName
     *            the full action name
     * @return the page action
     * @throws UnifyException
     *             if page action info with name is unknown
     */
    public Action getAction(String actionName) throws UnifyException {
        Action action = actionByNameMap.get(actionName);
        if (action == null) {
            throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_UNKNOWN_ACTION, getControllerName(), actionName);
        }

        return action;
    }

    /**
     * Retrieves all result mapping names associated with the page controller.
     */
    public Set<String> getResultNames() {
        return resultNameMap.keySet();
    }

    /**
     * Tests of page controller has result mapping with supplied name.
     * 
     * @param name
     *            the name to test with
     */
    public boolean hasResultWithName(String name) {
        return resultNameMap.containsKey(name);
    }

    /**
     * Gets a result mapping by specified name.
     * 
     * @param name
     *            the result mapping name
     * @return the result object
     * @throws UnifyException
     *             if result with name is unknown
     */
    public Result getResult(String name) throws UnifyException {
        Result result = resultNameMap.get(name);
        if (result == null) {
            throw new UnifyException(UnifyWebErrorConstants.CONTROLLER_UNKNOWN_RESULT, getControllerName(), name);
        }
        return result;
    }
}
