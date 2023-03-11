/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.font.FontSymbolManager;
import com.tcdng.unify.web.ui.widget.AbstractMultiControl;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * Control with up-down shift buttons
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-shiftbuttons")
public class ShiftButtons extends AbstractMultiControl implements RackButtons {

    private Control shiftTopCtrl;

    private Control shiftUpCtrl;

    private Control shiftDownCtrl;

    private Control shiftBottomCtrl;

    @Override
    public Control getShiftTopCtrl() {
        return shiftTopCtrl;
    }

    @Override
    public Control getShiftUpCtrl() {
        return shiftUpCtrl;
    }

    @Override
    public Control getShiftDownCtrl() {
        return shiftDownCtrl;
    }

    @Override
    public Control getShiftBottomCtrl() {
        return shiftBottomCtrl;
    }

    @Override
    public Control getDeleteCtrl() {
        return null;
    }

    @Override
    public String getShiftTopGroupId() throws UnifyException {
        return shiftTopCtrl.getGroupId();
    }

    @Override
    public String getShiftUpGroupId() throws UnifyException {
        return shiftUpCtrl.getGroupId();
    }

    @Override
    public String getShiftDownGroupId() throws UnifyException {
        return shiftDownCtrl.getGroupId();
    }

    @Override
    public String getShiftBottomGroupId() throws UnifyException {
        return shiftBottomCtrl.getGroupId();
    }

    @Override
    public String getDeleteGroupId() throws UnifyException {
        return null;
    }

    @Override
    protected void doOnPageConstruct() throws UnifyException {
        if (getComponentConfigs(FontSymbolManager.class).size() > 0) {
            shiftTopCtrl = (Control) addInternalChildWidget(
                    "!ui-symbol symbol:$s{angle-double-up} styleClass:$e{sbutton} hint:$m{move.to.top}", true, false);
            shiftUpCtrl = (Control) addInternalChildWidget(
                    "!ui-symbol symbol:$s{angle-up} styleClass:$e{sbutton} hint:$m{move.up}", true, false);
            shiftDownCtrl = (Control) addInternalChildWidget(
                    "!ui-symbol symbol:$s{angle-down} styleClass:$e{sbutton} hint:$m{move.down}", true, false);
            shiftBottomCtrl = (Control) addInternalChildWidget(
                    "!ui-symbol symbol:$s{angle-double-down} styleClass:$e{sbutton} hint:$m{move.to.bottom}", true,
                    false);
        } else {
            shiftTopCtrl = (Control) addInternalChildWidget(
                    "!ui-image src:$t{images/circle-top-arrow.png} styleClass:$e{sbutton} hint:$m{move.to.top}", true,
                    false);
            shiftUpCtrl = (Control) addInternalChildWidget(
                    "!ui-image src:$t{images/circle-up-arrow.png} styleClass:$e{sbutton} hint:$m{move.up}", true,
                    false);
            shiftDownCtrl = (Control) addInternalChildWidget(
                    "!ui-image src:$t{images/circle-down-arrow.png} styleClass:$e{sbutton} hint:$m{move.down}", true,
                    false);
            shiftBottomCtrl = (Control) addInternalChildWidget(
                    "!ui-image src:$t{images/circle-bottom-arrow.png} styleClass:$e{sbutton} hint:$m{move.to.bottom}",
                    true, false);
        }

        shiftTopCtrl.setGroupId(getPrefixedId("shft_"));
        shiftUpCtrl.setGroupId(getPrefixedId("shfu_"));
        shiftDownCtrl.setGroupId(getPrefixedId("shfd_"));
        shiftBottomCtrl.setGroupId(getPrefixedId("shfb_"));
    }
}
