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

import com.tcdng.unify.core.UnifyException;

/**
 * BasicDocument panels.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface DocumentPanels {

    /**
     * Returns the document id.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getId() throws UnifyException;

    /**
     * Returns the document header panel.
     * 
     * @return the header panel
     * @throws UnifyException
     *             if an error occurs
     */
    Panel getHeaderPanel() throws UnifyException;

    /**
     * Returns the document menu panel.
     * 
     * @return the menu panel
     * @throws UnifyException
     *             if an error occurs
     */
    Panel getMenuPanel() throws UnifyException;

    /**
     * Returns the document content panel.
     * 
     * @return the content panel
     * @throws UnifyException
     *             if an error occurs
     */
    ContentPanel getContentPanel() throws UnifyException;

    /**
     * Returns the document footer panel.
     * 
     * @return the footer panel
     * @throws UnifyException
     *             if an error occurs
     */
    Panel getFooterPanel() throws UnifyException;
}
