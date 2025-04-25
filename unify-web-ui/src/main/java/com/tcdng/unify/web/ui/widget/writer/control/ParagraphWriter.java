/*
 * Copyright 2018-2025 The Code Department.
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
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.Paragraph;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Paragraph writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Writes(Paragraph.class)
@Component("paragraph-writer")
public class ParagraphWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        Paragraph paragraph = (Paragraph) widget;
        writer.write("<p ");
        writeTagAttributes(writer, paragraph);
        writer.write(">");
        String value = paragraph.getStringValue();
        if (value == null) {
            if (StringUtils.isNotBlank(paragraph.getBinding())) {
                writer.writeHtmlFixedSpace();
            } else {
                writeCaption(writer, paragraph);
            }
        } else {
            writer.writeWithHtmlEscape(value);
        }
        writer.write("</p>");
    }

}
