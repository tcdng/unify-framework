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
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.ui.widget.AbstractMultiControl;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * Rich text editor.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-richtexteditor")
@UplAttributes({
	@UplAttribute(name = "rows", type = int.class) })
public class RichTextEditor extends AbstractMultiControl {

	private static final String DEFAULT_FONT_SIZE = "16px";

	private static final String DEFAULT_FONT_COLOR = "#000000";

	private static final int MIN_ROWS = 4;
	
	private Control boldCtrl;

	private Control italicCtrl;

	private Control underlineCtrl;

	private Control fontSizeCtrl;

	private Control fontColorCtrl;

	private Control leftAlignCtrl;

	private Control centerAlignCtrl;

	private Control rightAlignCtrl;

	private Control[] controls;

	private String fontSize;

	private String fontColor;
	
	public RichTextEditor() {
		this.fontSize = DEFAULT_FONT_SIZE;
		this.fontColor = DEFAULT_FONT_COLOR;
	}

	@Override
	public void addPageAliases() throws UnifyException {
		addPageAlias(fontSizeCtrl);
		addPageAlias(fontColorCtrl);
	}
	
    public int getRows() throws UnifyException {
        int rows = getUplAttribute(int.class, "rows");
        return rows < MIN_ROWS ? MIN_ROWS : rows;
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

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	@Override
	protected void doOnPageConstruct() throws UnifyException {
		boldCtrl = (Control) addInternalChildWidget("!ui-button symbol:$s{bold} styleClass:$e{btn}");
		italicCtrl = (Control) addInternalChildWidget("!ui-button symbol:$s{italic} styleClass:$e{btn}");
		underlineCtrl = (Control) addInternalChildWidget("!ui-button symbol:$s{underline} styleClass:$e{btn}");
		fontSizeCtrl = (Control) addInternalChildWidget("!ui-select list:$s{richtextfontsizelist} styleClass:$e{sel} binding:fontSize");
		fontColorCtrl = (Control) addInternalChildWidget(
				"!ui-select list:$s{richtextfontcolorlist} styleClass:$e{sel} binding:fontColor");
		leftAlignCtrl = (Control) addInternalChildWidget("!ui-button symbol:$s{align-left} styleClass:$e{btn}");
		centerAlignCtrl = (Control) addInternalChildWidget("!ui-button symbol:$s{align-center} styleClass:$e{btn}");
		rightAlignCtrl = (Control) addInternalChildWidget("!ui-button symbol:$s{align-right} styleClass:$e{btn}");

		controls = new Control[] { boldCtrl, italicCtrl, underlineCtrl, fontSizeCtrl, fontColorCtrl,
				leftAlignCtrl, centerAlignCtrl, rightAlignCtrl };
	}

}
