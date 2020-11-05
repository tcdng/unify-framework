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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.constant.ExtensionType;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.TextField;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Text field writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(TextField.class)
@Component("textfield-writer")
public class TextFieldWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        TextField textField = (TextField) widget;
        writeTextField(writer, textField, "text");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        TextField textField = (TextField) widget;
        super.doWriteBehavior(writer, textField);

        writer.beginFunction("ux.setTextRegexFormatting");
        if (textField.isUseFacade()) {
            writer.writeParam("pId", textField.getFacadeId());
        } else {
            writer.writeParam("pId", textField.getId());
        }
        writer.writeResolvedParam("pRegex", "\"" + getFormatRegex(textField) + "\"");
        writer.writeParam("pCase", textField.getCase());
        writer.endFunction();
        
        if(!textField.getExtensionType().isExtended()) {
            writeValueAccessor(writer, textField);
        }
    }

    protected void writeTextField(ResponseWriter writer, TextField textField, String type) throws UnifyException {
        writeTextField(writer, textField, type, textField.getExtensionType());
    }

    protected void writeLeadingAddOn(ResponseWriter writer, Widget widget) throws UnifyException {

    }

    protected void writeTrailingAddOn(ResponseWriter writer, Widget widget) throws UnifyException {

    }

    protected void writeBaseAddOn(ResponseWriter writer, Widget widget) throws UnifyException {

    }

    protected String getFormatRegex(TextField textField) throws UnifyException {
        return "";
    }

    private void writeTextField(ResponseWriter writer, TextField textField, String type, ExtensionType extensionType)
            throws UnifyException {
        if (extensionType.isExtended()) {
            writer.write("<div ");
            writeTagId(writer, textField.getBorderId());
            writeTagStyleClass(writer, textField);
            writeTagStyle(writer, textField);
            writer.write(">");
            writer.write("<div style=\"display:flex;width:100%;\">");

            writeLeadingAddOn(writer, textField);

            if (ExtensionType.FACADE_HIDDEN.equals(extensionType)) {
                writer.write("<input type=\"hidden\"");
                writeTagId(writer, textField);
                writeTagName(writer, textField);

                String value = textField.getStringValue();
                if (value != null) {
                    writer.write(" value=\"").writeWithHtmlEscape(value).write("\"");
                }
                writer.write("/>");
            }

            writeTextInput(writer, textField, type, extensionType);

            writeTrailingAddOn(writer, textField);
            writer.write("</div>");

            writeBaseAddOn(writer, textField);
            writer.write("</div>");
        } else {
            writeTextInput(writer, textField, type, extensionType);
        }
    }

    private void writeTextInput(ResponseWriter writer, TextField textField, String type, ExtensionType extensionType)
            throws UnifyException {
        writer.write("<input type=\"").write(type).write("\"");

        String value = null;
        if (extensionType.isExtended()) {
            if (extensionType.isFacade()) {
                writeTagId(writer, textField.getFacadeId());
//                value = textField.getFacadeStringValue();
            } else {
                writeTagId(writer, textField);
                writeTagName(writer, textField);
                value = textField.getStringValue();
            }

            writeTagStyleClass(writer, textField.getExtStyleClass());
            if (textField.getExtReadOnly()) {
                writeTagReadOnly(writer);
                writeTagDisabled(writer, textField);
            } else {
                writeTagEditAttributes(writer, textField);
            }

        } else {
            writeTagAttributes(writer, textField);
            value = textField.getStringValue();

            int size = textField.getUplAttribute(int.class, "size");
            if (size > 0) {
                writer.write(" size=\"").write(size).write("\"");
            }

            int maxLen = textField.getUplAttribute(int.class, "maxLen");
            if (maxLen > 0) {
                writer.write(" maxlength=\"").write(maxLen).write("\"");
            }

            if (textField.isUplAttribute("autocomplete")) {
                if (textField.getUplAttribute(boolean.class, "autocomplete")) {
                    writer.write(" autocomplete=\"on\"");
                } else {
                    writer.write(" autocomplete=\"off\"");
                }
            }
            
            writer.write(" spellcheck=\"").write(textField.isSpellCheck()).write("\"");
        }

        if (value != null) {
            writer.write(" value=\"");
            writer.writeWithHtmlEscape(value);
            writer.write("\"");
        }
        writer.write("/>");
    }
}
