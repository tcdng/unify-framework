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
import com.tcdng.unify.core.upl.UplComponent;

/**
 * Represents a user interface section.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface Section extends UplComponent {

    /**
     * Returns the section shortName
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getTag() throws UnifyException;

    /**
     * Returns the section caption
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getCaption() throws UnifyException;

    /**
     * Returns the section privilege
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getPrivilege() throws UnifyException;

    /**
     * Returns the section binding
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getBinding() throws UnifyException;

    /**
     * Returns the section component reference list
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> getReferences() throws UnifyException;

    /**
     * Returns true if section has binding
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isBinding() throws UnifyException;

    /**
     * Returns true if section does not display widgets captions
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isWidgetCaptionless() throws UnifyException;

    /**
     * Returns the section hidden attribute
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isHidden() throws UnifyException;
}
