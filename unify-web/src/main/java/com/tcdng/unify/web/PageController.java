/*
 * Copyright 2018-2019 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.Panel;

/**
 * Serves as the controller component of a page.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface PageController extends UserInterfaceController {

    /**
     * Returns page controller path information.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    PageControllerPathInfo getPathInfo() throws UnifyException;

    /**
     * Returns this page controller session ID
     */
    String getSessionId();

    /**
     * Returns the controller page
     */
    Page getPage();

    /**
     * Sets the controller page
     * 
     * @param page
     *            the page to bind to
     * @throws UnifyException
     *             if an error occurs
     */
    void setPage(Page page) throws UnifyException;

    /**
     * Returns a panel in the controller view by long name.
     * 
     * @param longName
     *            the panel long name
     * @throws UnifyException
     *             if an error occurs
     */
    Panel getPanelByLongName(String longName) throws UnifyException;

    /**
     * Returns a panel in the controller view by short name.
     * 
     * @param shortName
     *            the panel short name
     * @throws UnifyException
     *             if an error occurs
     */
    Panel getPanelByShortName(String shortName) throws UnifyException;

    /**
     * Executes a page index action.
     * 
     * @return the result mapping name
     * @throws UnifyException
     *             if an error occurs
     */
    String index() throws UnifyException;
}
