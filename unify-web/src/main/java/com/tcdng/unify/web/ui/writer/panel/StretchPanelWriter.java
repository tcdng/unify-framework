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
package com.tcdng.unify.web.ui.writer.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.panel.StretchPanel;
import com.tcdng.unify.web.ui.writer.AbstractPanelWriter;

/**
 * Stretch panel writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(StretchPanel.class)
@Component("stretchpanel-writer")
public class StretchPanelWriter extends AbstractPanelWriter {

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		StretchPanel panel = (StretchPanel) widget;
		writer.write("<div");
		writeTagId(writer, panel);
		writeTagStyleClass(writer, panel.getStyleClass());
		writeTagStyle(writer, "width:100%;height:100%;overflow:auto;");
		writer.write(">");
		writeInnerStructureAndContent(writer, panel);
		writer.write("</div>");
	}

	@Override
	public void writeInnerStructureAndContent(ResponseWriter writer, Panel panel) throws UnifyException {
		StretchPanel stretchPanel = (StretchPanel) panel;
		writer.write("<div id=\"").write(stretchPanel.getContentPageName());
		writer.write("\" style=\"width:100%;height:100%;display:none;\">");
		super.writeInnerStructureAndContent(writer, panel);
		writer.write("</div>");
	}

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
		super.doWriteBehavior(writer, widget);
		StretchPanel panel = (StretchPanel) widget;

		// Append switch panel rigging
		writer.write("ux.rigStretchPanel({");
		writer.write("\"pId\":\"").write(panel.getId()).write('"');
		writer.write(",\"pContId\":\"").write(panel.getContentPageName());
		writer.write("\"});");
	}

}
