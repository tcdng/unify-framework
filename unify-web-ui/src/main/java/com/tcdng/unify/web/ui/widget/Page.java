/*
 * Copyright 2018-2024 The Code Department.
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
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.ui.PageBean;
import com.tcdng.unify.web.ui.widget.panel.StandalonePanel;

/**
 * A user interface page.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface Page extends StandalonePanel {

    /**
     * Sets the page bean for this page.
     * 
     * @param pageBean
     *            the page bean to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setPageBean(PageBean pageBean) throws UnifyException;

    /**
     * Gets the page bean for this page.
     * 
     * @return the page bean
     * @throws UnifyException
     *             if an error occurs
     */
    PageBean getPageBean() throws UnifyException;

    /**
     * Returns stand-alone panel with specified name.
     * 
     * @param name
     *            the panel name.
     * 
     * @return the panel if found otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    StandalonePanel getStandalonePanel(String name) throws UnifyException;

    /**
     * Adds a standalone panel to this page.
     * 
     * @param name
     *            the standalone panel name
     * @param standalonePanel
     *            the standalone panel to add
     * @throws UnifyException
     *             if an error occurs
     */
    void addStandalonePanel(String name, StandalonePanel standalonePanel) throws UnifyException;

    /**
     * Removes a standalone panel by name.
     * 
     * @param name
     *            the panel name
     * @return the removed standalone panel otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    StandalonePanel removeStandalonePanel(String name) throws UnifyException;

    /**
     * Returns a panel contained in this page by long name.
     * 
     * @param longName
     *            the panel long name
     * @return the panel with the specified long name
     * @throws UnifyException
     *             if an error occurs
     */
    Panel getPanelByLongName(String longName) throws UnifyException;

    /**
     * Returns a panel contained in this page by short name.
     * 
     * @param shortName
     *            the panel short name
     * @return the panel with the specified short name
     * @throws UnifyException
     *             if an error occurs
     */
    Panel getPanelByShortName(String shortName) throws UnifyException;

    /**
     * Sets a page attribute.
     * 
     * @param name
     *            the attribute name
     * @param value
     *            the attribute value
     */
    void setAttribute(String name, Object value);

    /**
     * Returns a page attribute.
     * 
     * @param name
     *            the name of the attribute
     * @return the attribute value if found otherwise null
     */
    Object getAttribute(String name);

    /**
     * Returns a page attribute.
     * 
     * @param dataType the type to convert to
     * @param name
     *            the name of the attribute
     * @return the attribute value
     */
    <T> T getAttribute(Class<T> dataType, String name) throws UnifyException;

    /**
     * Returns true if page attribute.
     * 
     * @param name
     *            the name of the attribute
     * @return true if page attribute otherwise false
     */
    boolean isAttribute(String name);

    /**
     * Removes a page attribute.
     * 
     * @param name
     *            the name of the attribute
     * @return the attribute value if found otherwise null
     */
    Object removeAttribute(String name);

    /**
     * Clears a page attribute.
     * 
     * @param name
     *            the name of the attribute
     * @return the attribute value if found otherwise null
     */
    Object clearAttribute(String name);

    /**
     * Returns true if page is document.
     */
    boolean isDocument();

	/**
	 * Sets this page path parts
	 * 
	 * @param controllerPathParts the path parts to set
	 * @param pageId              the page ID to set.
	 * 
	 */
	void setPathParts(ControllerPathParts controllerPathParts, String pageId);

    /**
     * Returns the page ID
     */
    String getPageId();

    /**
     * Returns the page path ID
     */
    String getPathId();

    /**
     * Returns the page path variable
     */
    List<String> getPathVariables();

    /**
     * Returns popup base ID
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getPopupBaseId() throws UnifyException;

    /**
     * Returns popup window ID
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getPopupWinId() throws UnifyException;

    /**
     * Returns popup sys ID
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getPopupSysId() throws UnifyException;
    
    /**
     * Returns page sub-caption.
     * @throws UnifyException if an error occurs
     */
    String getSubCaption() throws UnifyException;
}
