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

package com.tcdng.unify.web.ui;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.constant.ClosePageMode;

/**
 * Document content panel.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ContentPanel extends Panel {

    /**
     * Gets the busy indicator component ID for this content panel.
     * 
     * @return the indicator ID
     * @throws UnifyException
     *             if an error occurs
     */
    String getBusyIndicatorId() throws UnifyException;

    /**
     * Gets the current active page.
     * 
     * @return the current page
     * @throws UnifyException
     *             if an error occurs
     */
    Page getCurrentPage() throws UnifyException;

    /**
     * Adds page to content.
     * 
     * @param page
     *            the page to add
     * @throws UnifyException
     *             if an error occurs
     */
    void addContent(Page page) throws UnifyException;

    /**
     * Evaluates page close event.
     * 
     * @param page
     *            the page with close event
     * @param closePageMode
     *            the close page mode
     * @return list of path IDs for pages to be closed
     * @throws UnifyException
     */
    List<String> evaluateRemoveContent(Page page, ClosePageMode closePageMode) throws UnifyException;

    /**
     * Remove pages specified by supplied path IDs
     * 
     * @param toRemovePathIdList
     *            the path IDs to use
     * @throws UnifyException
     *             if an error occurs
     */
    void removeContent(List<String> toRemovePathIdList) throws UnifyException;
}
