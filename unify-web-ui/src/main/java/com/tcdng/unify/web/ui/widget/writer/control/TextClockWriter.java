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
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.TextClock;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Text clock writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(TextClock.class)
@Component("textclock-writer")
public class TextClockWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        TextClock textClock = (TextClock) widget;
        writer.write("<div");
        writeTagAttributes(writer, textClock);
        writer.write(">");
        writer.write("<span");
        writeTagStyleClass(writer, "tctitle");
        writer.write(">");
        writer.write(textClock.getDateTitle());
        writer.write("</span>");
        writer.writeHtmlFixedSpace();
        writer.write("<span");
        writeTagId(writer, textClock.getDateId());
        writeTagStyleClass(writer, "tccontent");
        writer.write(">");
        writer.write("</span>");
        writer.writeHtmlFixedSpace();
        writer.write("<span");
        writeTagStyleClass(writer, "tctitle");
        writer.write(">");
        writer.write(textClock.getTimeTitle());
        writer.write("</span>");
        writer.writeHtmlFixedSpace();
        writer.write("<span");
        writeTagId(writer, textClock.getTimeId());
        writeTagStyleClass(writer, "tccontent");
        writer.write(">");
        writer.write("</span>");
        writer.write("</div>");
    }

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);

		// Append rigging
		TextClock textClock = (TextClock) widget;
		writer.beginFunction("ux.rigTextClock");
		writer.writeParam("pId", textClock.getId());
		writer.writeParam("pDateId", textClock.getDateId());
		writer.writeParam("pTimeId", textClock.getTimeId());
		writer.endFunction();
	}

}
