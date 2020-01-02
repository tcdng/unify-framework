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

package com.tcdng.unify.web.ui.panel;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.ValueStore;

/**
 * Switch panel handler.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface SwitchPanelHandler extends UnifyComponent {

    /**
     * Handles a switch.
     * 
     * @param switchPanel
     *            the calling switch panel
     * 
     * @param compShortName
     *            short name of the component switched to
     * @param valueStore
     *            the switch panel value store
     * @param isNewSwitch
     *            indicates of new tab component is switched to
     * @throws UnifyException
     *             if an error occurs
     */
    void handleSwitchContent(SwitchPanel switchPanel, String compShortName, ValueStore valueStore, boolean isNewSwitch)
            throws UnifyException;
}
