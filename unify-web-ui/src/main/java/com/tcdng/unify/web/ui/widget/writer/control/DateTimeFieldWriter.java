/*
 * Copyright 2018-2022 The Code Department.
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
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.DateTimeField;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Date-time writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(DateTimeField.class)
@Component("datetime-writer")
public class DateTimeFieldWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        DateTimeField dateTimeField = (DateTimeField) widget;
        writer.write("<div");
        writeTagAttributes(writer, dateTimeField);
        writer.write(">");

        writer.write("<div style=\"display:table; width:100%;\">");
        writer.write("<div style=\"display:table-row;\">");
        writer.write("<div class=\"dtfdatecell\" style=\"display:table-cell;\">");
        writer.writeStructureAndContent(dateTimeField.getDateCtrl());
        writer.write("</div>");
        writer.write("<div class=\"dtftimecell\" style=\"display:table-cell;\">");
        writer.writeStructureAndContent(dateTimeField.getTimeCtrl());
        writer.write("</div>");
        writer.write("</div></div>");
        
        writer.write("</div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);
        DateTimeField dateTimeField = (DateTimeField) widget;
        writer.writeBehavior(dateTimeField.getDateCtrl());
        writer.writeBehavior(dateTimeField.getTimeCtrl());
    }
}
