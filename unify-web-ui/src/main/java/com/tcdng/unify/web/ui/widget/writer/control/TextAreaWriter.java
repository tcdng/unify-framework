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
package com.tcdng.unify.web.ui.widget.writer.control;

import java.util.Collection;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.TextArea;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Text area writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(TextArea.class)
@Component("textarea-writer")
public class TextAreaWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        writeTextArea(writer, (TextArea) widget, null);
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);
        TextArea textArea = (TextArea) widget;
        writer.beginFunction("ux.rigTextArea");
        writer.writeParam("pId", textArea.getId());
        writer.writeParam("pScrEnd", textArea.isScrollToEnd());
        writer.endFunction();
    }

    @SuppressWarnings("unchecked")
    protected void writeTextArea(ResponseWriter writer, TextArea textArea, String styleClass) throws UnifyException {
        writer.write("<textarea");
        if (StringUtils.isBlank(styleClass)) {
            writeTagAttributes(writer, textArea);
        } else {
            writeTagAttributesUsingStyleClass(writer, textArea, styleClass);
        }
        int columns = textArea.getColumns();
        if (columns > 0) {
            writer.write(" cols=\"").write(columns).write("\"");
        }

        int rows = textArea.getRows();
        if (rows > 0) {
            writer.write(" rows=\"").write(rows).write("\"");
        }

        int maxLen = textArea.getMaxLen();
        if (maxLen > 0) {
            writer.write(" maxlength=\"").write(maxLen).write("\"");
        }

        if (textArea.isWordWrap()) {
            writer.write(" wrap=\"virtual\"");
        } else {
            writer.write(" wrap=\"off\"");
        }

        writer.write(" spellcheck=\"").write(textArea.isSpellCheck()).write("\"");
        if (textArea.isAutoComplete()) {
            writer.write(" autocomplete=\"on\"");
        } else {
            writer.write(" autocomplete=\"off\"");
        }
        
        if (textArea.getTabIndex() >= 0) {
            writer.write(" tabindex=\"").write(textArea.getTabIndex()).write("\"");
        }
        writer.write(">");

        Object value = textArea.getValue();
        if (value != null) {
            if (value instanceof String[]) {
                for (String line : (String[]) value) {
                    writer.writeWithHtmlEscape(line);
                    writer.writeWithHtmlEscape("\n");
                }
            } else if (value instanceof Collection) {
                for (Object obj : (Collection<Object>) value) {
                    writer.writeWithHtmlEscape(String.valueOf(obj));
                    writer.writeWithHtmlEscape("\n");
                }
            } else {
                writer.writeWithHtmlEscape(textArea.getStringValue());
            }
        }
        writer.write("</textarea>");
    }
}
