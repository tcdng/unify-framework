/*
 * Copyright 2018 The Code Department
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.web.ui.AbstractMultiControl.ChildControlInfo;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.TitleBar;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Title bar writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(TitleBar.class)
@Component("titlebar-writer")
public class TitleBarWriter extends AbstractControlWriter {

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		TitleBar titleBar = (TitleBar) widget;
		writer.write("<div");
		writeTagAttributes(writer, titleBar);
		writer.write(">");
		writer.write("<div class=\"tblabel\">");
		writeCaption(writer, titleBar);
		writer.write("</div>");
		writer.write("<div class=\"tbcontrols\">");
		for (ChildControlInfo childControlInfo : titleBar.getChildControlInfos()) {
			if (childControlInfo.isExternal() && childControlInfo.isPrivilegeVisible()) {
				writer.writeStructureAndContent(childControlInfo.getControl());
			}
		}
		writer.write("</div>");
		writer.write("<div style=\"clear:both;\"></div>");
		writer.write("</div>");
	}

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
		super.doWriteBehavior(writer, widget);
		TitleBar titleBar = (TitleBar) widget;
		if (titleBar.getUplAttribute(boolean.class, "draggable")) {
			// Append drag and drop JS
			writer.write("ux.rigDragAndDropPopup({");
			writer.write("\"pId\":\"").write(titleBar.getId()).write("\"});");
		}

		// Append external controls behavior
		ValueStore valueStore = titleBar.getValueStore();
		for (ChildControlInfo childControlInfo : titleBar.getChildControlInfos()) {
			if (childControlInfo.isExternal() && childControlInfo.isPrivilegeVisible()) {
				Control control = childControlInfo.getControl();
				ValueStore origValueStore = control.getValueStore();
				control.setValueStore(valueStore);
				writer.writeBehaviour(childControlInfo.getControl());
				control.setValueStore(origValueStore);
			}
		}
	}
}
