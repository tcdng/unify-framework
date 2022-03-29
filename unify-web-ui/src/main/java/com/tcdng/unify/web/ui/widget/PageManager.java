/*
 * Copyright 2018-2022 The Code Department.
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

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.widget.panel.StandalonePanel;

/**
 * Manages the creation page components.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface PageManager extends UnifyComponent {

    /**
     * Returns configured common document style sheets for this container.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> getDocumentStyleSheets() throws UnifyException;

    /**
     * Returns configured common document script for this container.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> getDocumentsScripts() throws UnifyException;

    /**
     * Creates a new page instance from document associated with specified page
     * controller and for specified locale.
     * 
     * @param locale
     *            the locale
     * @param name
     *            the page controller name
     * @return the page instance
     * @throws UnifyException
     *             If an error occurs
     */
    Page createPage(Locale locale, String name) throws UnifyException;

    /**
     * Invalidates a document if it is stale.
     * 
     * @param name
     *            the document name
     * @return a true value if document was invalidated
     * @throws UnifyException
     *             if an error occurs
     */
    boolean invalidateStaleDocument(String name) throws UnifyException;

    /**
     * Creates a new stand-alone panel instance from document associated with a
     * panel component type.
     * 
     * @param locale
     *            the locale
     * @param name
     *            the panel name
     * @return the panel instance
     * @throws UnifyException
     *             If an error occurs
     */
    StandalonePanel createStandalonePanel(Locale locale, String name) throws UnifyException;

    /**
     * Gets the property bindings by page name for specified standalone panel.
     * 
     * @param name
     *            the stand-alone panel name
     * @return the property bindings
     * @throws UnifyException
     *             if an error occurs
     */
    Map<String, PropertyInfo> getStandalonePanelPropertyBindings(String name) throws UnifyException;

    /**
     * Returns the page name for supplied long name. The same page name is always
     * returned for a particular long name.
     * 
     * @param longName
     *            the long name
     * @throws UnifyException
     *             if an error occurs
     */
    String getPageName(String longName) throws UnifyException;

    /**
     * Returns the page names for supplied long names. Supplied long names are
     * registered by the manager.
     * 
     * @param longNames
     *            the supplied long names
     * @return an array of page names in sequence corresponding to supplied long
     *         name array
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> getPageNames(Collection<String> longNames) throws UnifyException;

    /**
     * Returns the registered long name for supplied page name.
     * 
     * @param pageName
     *            the supplied page name
     * @throws UnifyException
     *             if page name is unknown.
     */
    String getLongName(String pageName) throws UnifyException;

    /**
     * Fetches long names for supplied page names.
     * 
     * @param pageNames
     *            the page names
     * @return long names
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> getLongNames(Collection<String> pageNames) throws UnifyException;

    /**
     * Gets the expanded component page name list of a UPL component by page name.
     * 
     * @param pageName
     *            the UPL component page name
     * @return the expanded component list if one exists for supplied page name,
     *         otherwise null
     * @throws UnifyException
     *             If component with supplied page name is unknown
     */
    List<String> getExpandedReferences(String pageName) throws UnifyException;

    /**
     * Gets the expanded component page name list of a UPL component by collection of long names.
     * 
     * @param longNames
     *            the UPL component long names
     * @return the expanded component list if one exists for supplied long names,
     *         otherwise null
     * @throws UnifyException
     *             If an error occurs
     */
    List<String> getExpandedReferencesForLongNames(Collection<String> longNames) throws UnifyException;

    /**
     * Gets the expanded component page name list of a UPL component by collection of page names.
     * 
     * @param pageNames
     *            the UPL component page names
     * @return the expanded component list if one exists for supplied page names,
     *         otherwise null
     * @throws UnifyException
     *             If an error occurs
     */
    List<String> getExpandedReferencesForPageNames(Collection<String> pageNames) throws UnifyException;

    /**
     * Gets the value component page name list of a UPL component by page name.
     * 
     * @param pageName
     *            the UPL component page name
     * @return the value component list if one exists for supplied page name,
     *         otherwise null
     * @throws UnifyException
     *             If component with supplied page name is unknown
     */
    List<String> getValueReferences(String pageName) throws UnifyException;
}
