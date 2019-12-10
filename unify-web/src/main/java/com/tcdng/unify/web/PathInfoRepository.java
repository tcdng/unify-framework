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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.Page;

/**
 * Path information repository.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface PathInfoRepository extends UnifyComponent {

    /**
     * Gets page path information.
     * 
     * @param page
     *            the page object.
     * 
     * @return the page path information
     * @throws UnifyException
     *             if an error occurs
     */
    PagePathInfo getPagePathInfo(Page page) throws UnifyException;

    /**
     * Gets the supplied page path parts
     * 
     * @param page
     *            the page object
     * @return the part parts
     * @throws UnifyException
     *             if an error occurs
     */
    PathParts getPathParts(Page page) throws UnifyException;

    /**
     * Gets the supplied path path parts
     * 
     * @param path
     *            the path
     * @return the part parts
     * @throws UnifyException
     *             if an error occurs
     */
    PathParts getPathParts(String path) throws UnifyException;
}
