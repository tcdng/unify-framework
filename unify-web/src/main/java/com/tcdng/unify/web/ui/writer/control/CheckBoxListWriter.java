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
package com.tcdng.unify.web.ui.writer.control;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.web.ui.PushType;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.CheckBoxList;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Check box list writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(CheckBoxList.class)
@Component("checkboxlist-writer")
public class CheckBoxListWriter extends AbstractControlWriter {

	@SuppressWarnings("unchecked")
	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		CheckBoxList checkBoxList = (CheckBoxList) widget;
		this.writeHiddenPush(writer, checkBoxList, PushType.CHECKBOX);

		List<String> values = checkBoxList.getValue(ArrayList.class, String.class);
		List<? extends Listable> listableList = checkBoxList.getListables();
		int breaks = listableList.size();
		boolean appendSym = !checkBoxList.getUplAttribute(boolean.class, "flow");
		for (Listable listable : listableList) {
			writer.write("<input type=\"checkbox\"");
			this.writeTagName(writer, checkBoxList);
			this.writeTagStyleClass(writer, checkBoxList);
			this.writeTagStyle(writer, checkBoxList);

			String key = listable.getListKey();
			if (values != null && values.contains(key)) {
				writer.write(" checked=\"checked\"");
			}
			writer.write(" value=\"").write(key).write("\"/>");
			writer.writeWithHtmlEscape(listable.getListDescription());
			if (appendSym && ((--breaks) > 0)) {
				writer.write("<br />");
			}
		}
	}
}
