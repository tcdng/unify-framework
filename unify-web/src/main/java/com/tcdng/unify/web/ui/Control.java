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
package com.tcdng.unify.web.ui;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.TriState;
import com.tcdng.unify.web.ui.control.ControlColorMode;

/**
 * A user interface component that users can interact with.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface Control extends DataTransferWidget {

    /**
     * Tests if the component is required and privilege exists in the current user
     * role.
     * 
     * @return true if component is required and user role has component privilege
     */
    TriState getRequired() throws UnifyException;

    /**
     * Sets control's required state
     * 
     * @param required
     *            the state to set
     * @throws UnifyException
     *             if an error occurs
     */
    void setRequired(TriState required) throws UnifyException;

    /**
     * Returns the control non-indexed Id
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getBaseId() throws UnifyException;

    /**
     * Returns the control border Id
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getBorderId() throws UnifyException;

    /**
     * Returns the control notification Id
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getNotificationId() throws UnifyException;

    /**
     * Updates control state.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void updateState() throws UnifyException;

    /**
     * Sets focus on this control.
     * 
     * @return a true value if focused otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    boolean setFocus() throws UnifyException;

    /**
     * Tests if control focus attribute is true.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isFocus() throws UnifyException;

    /**
     * Sets the control color mode.
     * 
     * @param colorMode
     *            the color mode to set
     */
    void setColorMode(ControlColorMode colorMode);

    /**
     * Gets the control color mode.
     * 
     * @return the color mode
     */
    ControlColorMode getColorMode();

    /**
     * Checks of control color mode should be rendered on layout.
     * 
     * @return a true value is layout color mode should be used
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isLayoutColorMode() throws UnifyException;
}
