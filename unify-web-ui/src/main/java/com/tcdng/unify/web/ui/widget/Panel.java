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

import com.tcdng.unify.core.UnifyException;

/**
 * A user interface panel.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface Panel extends Container {

    /**
     * Switches panel state.
     */
    void switchState() throws UnifyException;

    /**
     * Resets panel state.
     */
    void resetState() throws UnifyException;

    /**
     * Returns true if panel allows automatic refresh.
     */
    boolean isAllowRefresh();

    /**
     * Adds an event listener to this panel.
     * 
     * @param listener
     *            the listener to add
     */
    void addEventListener(PanelEventListener listener);

    /**
     * Removes an event listener from this panel.
     * 
     * @param listener
     *            the listener to remove
     */
    void removeEventListener(PanelEventListener listener);

    /**
     * Returns the panel background image.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getBackImageSrc() throws UnifyException;

    /**
     * Returns the panel refresh path.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getRefreshPath() throws UnifyException;

    /**
     * Returns the panel refresh period.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    int getRefreshEvery() throws UnifyException;

    /**
     * Returns true if panel refresh is based on user activity on client device.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isRefreshOnUserAct() throws UnifyException;

    /**
     * Returns the panel legend.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getLegend() throws UnifyException;
}
