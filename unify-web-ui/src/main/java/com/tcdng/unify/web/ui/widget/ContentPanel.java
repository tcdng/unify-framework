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

package com.tcdng.unify.web.ui.widget;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.constant.ClosePageMode;

/**
 * Document content panel.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ContentPanel extends Panel {

	/**
	 * Gets sticky paths.
	 * 
	 * @return the sticky paths
	 * @throws UnifyException if an error occurs
	 */
	String[] getPaths() throws UnifyException;
	
	/**
	 * Returns true if content size is zero.
	 * @throws UnifyException if an error occurs
	 */
	boolean isBlankContent() throws UnifyException;
	
	/**
	 * Return true if content panel is in detach window.
	 * @throws UnifyException if an error occurs
	 */
	boolean isDetachedWindow() throws UnifyException;
	
    /**
     * Gets the base content ID.
     * 
     * @return the base content ID
     * @throws UnifyException
     *                        if an error occurs
     */
    String getBaseContentId() throws UnifyException;

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
     * Insert page to content at current page.
     * 
     * @param page
     *            the page to insert
     * @return the path ID of shifted page otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    String insertContent(Page page) throws UnifyException;

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
    
    /**
     * Clears content panel pages.
     * 
     * @throws UnifyException
     *                        if an error occurs
     */
    void clearPages() throws UnifyException;
}
