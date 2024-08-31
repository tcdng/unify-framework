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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.ui.widget.AbstractMultiControl;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * Rich text editor.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-richtexteditor")
public class RichTextEditor extends AbstractMultiControl {

	private Control boldCtrl;

	private Control italicCtrl;

	private Control underlineCtrl;

	private Control fontSizeCtrl;

	private Control fontColorCtrl;

	private Control backColorCtrl;

	private Control leftAlignCtrl;

	private Control centerAlignCtrl;

	private Control rightAlignCtrl;

	private Control[] controls;

	@Override
	protected void doOnPageConstruct() throws UnifyException {
		boldCtrl = (Control) addInternalChildWidget("!ui-symbol symbol:$s{bold} styleClass:$e{btn}");
		italicCtrl = (Control) addInternalChildWidget("!ui-symbol symbol:$s{italic} styleClass:$e{btn}");
		underlineCtrl = (Control) addInternalChildWidget("!ui-symbol symbol:$s{underline} styleClass:$e{btn}");
		fontSizeCtrl = (Control) addInternalChildWidget("!ui-select list:$s{richtextfontsizelist} styleClass:$e{sel}");
		fontColorCtrl = (Control) addInternalChildWidget(
				"!ui-select list:$s{richtextfontcolorlist} styleClass:$e{sel}");
		backColorCtrl = (Control) addInternalChildWidget(
				"!ui-select list:$s{richtextbackcolorlist} styleClass:$e{sel}");
		leftAlignCtrl = (Control) addInternalChildWidget("!ui-symbol symbol:$s{align-left} styleClass:$e{btn}");
		centerAlignCtrl = (Control) addInternalChildWidget("!ui-symbol symbol:$s{align-center} styleClass:$e{btn}");
		rightAlignCtrl = (Control) addInternalChildWidget("!ui-symbol symbol:$s{align-right} styleClass:$e{btn}");

		controls = new Control[] { boldCtrl, italicCtrl, underlineCtrl, fontSizeCtrl, fontColorCtrl, backColorCtrl,
				leftAlignCtrl, centerAlignCtrl, rightAlignCtrl };
	}

	public String getToolBarId() throws UnifyException {
		return getPrefixedId("tbr_");
	}

	public String getEditorId() throws UnifyException {
		return getPrefixedId("etr_");
	}

	public Control getBoldCtrl() {
		return boldCtrl;
	}

	public Control getItalicCtrl() {
		return italicCtrl;
	}

	public Control getUnderlineCtrl() {
		return underlineCtrl;
	}

	public Control getFontSizeCtrl() {
		return fontSizeCtrl;
	}

	public Control getFontColorCtrl() {
		return fontColorCtrl;
	}

	public Control getBackColorCtrl() {
		return backColorCtrl;
	}

	public Control getLeftAlignCtrl() {
		return leftAlignCtrl;
	}

	public Control getCenterAlignCtrl() {
		return centerAlignCtrl;
	}

	public Control getRightAlignCtrl() {
		return rightAlignCtrl;
	}

	public Control[] getControls() {
		return controls;
	}

}
