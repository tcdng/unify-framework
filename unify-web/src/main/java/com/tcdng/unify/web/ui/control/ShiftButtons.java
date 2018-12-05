/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.web.ui.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.ui.AbstractMultiControl;
import com.tcdng.unify.web.ui.Control;

/**
 * Control with up-down shift buttons
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-shiftbuttons")
public class ShiftButtons extends AbstractMultiControl {

	private Control shiftTopCtrl;

	private Control shiftUpCtrl;

	private Control shiftDownCtrl;

	private Control shiftBottomCtrl;

	@Override
	public void onPageInitialize() throws UnifyException {
		super.onPageInitialize();
		this.shiftTopCtrl = this.addInternalChildControl(
				"!ui-image src:$t{images/circle-top-arrow.png} styleClass:$e{sbutton} hint:$m{move.to.top}", true,
				false);
		this.shiftUpCtrl = this.addInternalChildControl(
				"!ui-image src:$t{images/circle-up-arrow.png} styleClass:$e{sbutton} hint:$m{move.up}", true, false);
		this.shiftDownCtrl = this.addInternalChildControl(
				"!ui-image src:$t{images/circle-down-arrow.png} styleClass:$e{sbutton} hint:$m{move.down}", true,
				false);
		this.shiftBottomCtrl = this.addInternalChildControl(
				"!ui-image src:$t{images/circle-bottom-arrow.png} styleClass:$e{sbutton} hint:$m{move.to.bottom}", true,
				false);

		this.shiftTopCtrl.setGroupId(this.getPrefixedId("shft_"));
		this.shiftUpCtrl.setGroupId(this.getPrefixedId("shfu_"));
		this.shiftDownCtrl.setGroupId(this.getPrefixedId("shfd_"));
		this.shiftBottomCtrl.setGroupId(this.getPrefixedId("shfb_"));
	}

	public Control getShiftTopCtrl() {
		return this.shiftTopCtrl;
	}

	public Control getShiftUpCtrl() {
		return this.shiftUpCtrl;
	}

	public Control getShiftDownCtrl() {
		return this.shiftDownCtrl;
	}

	public Control getShiftBottomCtrl() {
		return this.shiftBottomCtrl;
	}

	public String getShiftTopGroupId() throws UnifyException {
		return this.shiftTopCtrl.getGroupId();
	}

	public String getShiftUpGroupId() throws UnifyException {
		return this.shiftUpCtrl.getGroupId();
	}

	public String getShiftDownGroupId() throws UnifyException {
		return this.shiftDownCtrl.getGroupId();
	}

	public String getShiftBottomGroupId() throws UnifyException {
		return this.shiftBottomCtrl.getGroupId();
	}
}
