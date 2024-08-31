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
package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.RichTextEditor;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Rich text editor writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(RichTextEditor.class)
@Component("richtexteditor-writer")
public class RichTextEditorWriter extends AbstractControlWriter {

	private static final int ROW_HEIGHT_PIXELS = 16;

	@Override
	protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
		RichTextEditor editor = (RichTextEditor) widget;
		writer.write("<div ");
		writeTagId(writer, editor.getFacadeId());
		writeTagStyleClass(writer, editor);
		writeTagStyle(writer, editor);
		writer.write(">");

		writer.write("<div ");
		writeTagId(writer, editor.getToolBarId());
		writeTagStyleClass(writer, "toolbar");
		writer.write(" style=\"display:table;width:100%;\">");
		writer.write("<div style=\"display:table-row;\">");
		for (Control ctrl : editor.getControls()) {
			writer.write("<div class=\"ctrl\" style=\"display:table-cell;\">");
			writer.writeStructureAndContent(ctrl);
			writer.write("</div>");
		}
		writer.write("</div>");
		writer.write("</div>");

		writer.write("<div ");
		writeTagId(writer, editor.getEditorId());
		writeTagStyleClass(writer, "editor");
		writer.write(" style=\"width:100%;height:").write(editor.getRows() * ROW_HEIGHT_PIXELS).write("px;\"");
		if (editor.isContainerEditable()) {
			writer.write(" contenteditable=\"true\"");
		}
		writer.write(">");

		writer.write("</div>");

		writer.write("<input type=\"hidden\"");
		writeTagId(writer, editor);
		writeTagName(writer, editor);

		String value = editor.getStringValue();
		if (value != null) {
			writer.write(" value=\"").writeWithHtmlEscape(value).write("\"");
		}
		writer.write("/>");

		writer.write("</div>");
	}

	@Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);

		RichTextEditor editor = (RichTextEditor) widget;
		for (Control ctrl : editor.getControls()) {
			writer.writeBehavior(ctrl);
		}

		// Append rigging
		writer.beginFunction("ux.rigRichTextEditor");
		writer.writeParam("pId", editor.getId());
		writer.writeCommandURLParam("pCmdURL");
		writer.writeParam("pContId", editor.getContainerId());
		writer.writeParam("pEditable", editor.isContainerEditable());
		writer.endFunction();
	}

}
