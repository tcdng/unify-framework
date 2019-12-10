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

/**
 * Serves as the controller component of a page.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface PageController<T extends PageBean> extends UIController {

    /**
     * Gets the page bean class associated with this controller.
     * 
     * @return the page bean class
     */
    Class<T> getPageBeanClass();

    /**
     * Gets the current page associated with controller.
     * @return the page object
     * @throws UnifyException if an error occurs
     */
    Page getPage() throws UnifyException;
    
    /**
     * Initializes current page.
     * 
     * @throws UnifyException if an error occurs
     */
    void initPage() throws UnifyException;
    
    /**
     * Executes a page index action.
     * 
     * @return the result mapping name
     * @throws UnifyException
     *             if an error occurs
     */
    String indexPage() throws UnifyException;

    /**
     * Executes a page open action.
     * 
     * @return the result mapping name
     * @throws UnifyException
     *             if an error occurs
     */
    String openPage() throws UnifyException;

    /**
     * Executes a page save action.
     * 
     * @return the result mapping name
     * @throws UnifyException
     *             if an error occurs
     */
    String savePage() throws UnifyException;

    /**
     * Executes a page close action.
     * 
     * @return the result mapping name
     * @throws UnifyException
     *             if an error occurs
     */
    String closePage() throws UnifyException;
}
