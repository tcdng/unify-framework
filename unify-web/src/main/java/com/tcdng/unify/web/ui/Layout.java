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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.upl.UplComponent;

/**
 * Used for managing a container layout.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface Layout extends UplComponent {

    /**
     * Returns layout show caption.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isShowCaption() throws UnifyException;

    /**
     * Returns layout inline caption flag.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isInlineCaption() throws UnifyException;

    /**
     * Returns layout inline caption style class.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getInlineCaptionClass() throws UnifyException;
    
    /**
     * Returns the layout's caption style.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getCaptionStyle() throws UnifyException;

    /**
     * Returns the layout's caption suffix.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getCaptionSuffix() throws UnifyException;

    /**
     * Returns layout style class.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getStyleClass() throws UnifyException;

    /**
     * Returns layout style.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getStyle() throws UnifyException;
}
