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
package com.tcdng.unify.web.ui.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.TextClock;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Text clock writer.
 * 
 * @author Lateef Ojulari
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
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);

        // Append rigging
        TextClock textClock = (TextClock) widget;
        writer.write("ux.rigTextClock({");
        writer.write("\"pId\":\"").write(textClock.getId()).write('"');
        writer.write(",\"pDateId\":\"").write(textClock.getDateId()).write('"');
        writer.write(",\"pTimeId\":\"").write(textClock.getTimeId()).write('"');
        writer.write("});");
    }

}
