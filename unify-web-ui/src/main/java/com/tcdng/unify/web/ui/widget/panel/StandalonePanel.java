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
package com.tcdng.unify.web.ui.widget.panel;

import java.util.List;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.PageWidgetValidator;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.PageAction;
import com.tcdng.unify.web.ui.widget.PageManager;
import com.tcdng.unify.web.ui.widget.PageValidation;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.StandalonePanelInfo;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * Standalone panel interface.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface StandalonePanel extends Panel {

    /**
     * Sets the panel info.
     * 
     * @param standalonePanelInfo
     *            the standalone panel information
     */
    void setStandalonePanelInfo(StandalonePanelInfo standalonePanelInfo);

    /**
     * Returns the page validator for this panel.
     * 
     * @param pageManager
     *            the page manager
     * @param longName
     *            the page validation long name
     * @throws UnifyException
     *             if an error occurs
     */
    PageWidgetValidator getPageWidgetValidator(PageManager pageManager, String longName) throws UnifyException;

    /**
     * Returns page validation by long name.
     * 
     * @param longName
     *            the validation long name
     */
    PageValidation getPageValidation(String longName);

    /**
     * Returns this panel's page validation long names.
     */
    Set<String> getPageValidationNames();

    /**
     * Returns page action by long name.
     * 
     * @param longName
     *            the page action long name
     */
    PageAction getPageAction(String longName);

    /**
     * Resolve page actions for supplied event handlers.
     * 
     * @param eventHandlers
     *            the event handlers
     * @throws UnifyException
     *             if an error occurs.
     */
    void resolvePageActions(EventHandler[] eventHandlers) throws UnifyException;

    /**
     * Gets widgets by long names.
     * 
     * @param longNames
     *            - the component long names
     * @return array of components
     * @throws UnifyException
     *             if component in long name list doesn't exist on page
     */
    List<Widget> getWidgetsByLongNames(List<String> longNames) throws UnifyException;

    /**
     * Returns true if page validation is enabled.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isValidationEnabled() throws UnifyException;

    /**
     * Sets the page validation enabled flag.
     * 
     * @param validationEnabled
     *            the flag to set
     */
    void setValidationEnabled(boolean validationEnabled);

    /**
     * Returns true if source is invalidated.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isSourceInvalidated() throws UnifyException;
}
