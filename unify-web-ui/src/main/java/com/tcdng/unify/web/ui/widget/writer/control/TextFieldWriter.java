/*
 * Copyright 2018-2023 The Code Department.
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
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.constant.ExtensionType;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.TextField;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Text field writer.
 * 
 * @author The Code Department
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
        
        writer.writeParam("pHid", textField.getId());
        writer.writeParam("pMimic", textField.isHiddenMimic());
        writer.writeResolvedParam("pRegex", "\"" + getFormatRegex(textField) + "\"");
        if (textField.getCase() != null) {
            writer.writeParam("pCase", textField.getCase().toString().toLowerCase());
        }
        
        if (textField.isClientFormat()) {
            JsonWriter jw = new JsonWriter();
            jw.beginObject();
            addClientFormatParams(textField, jw);
            jw.endObject();
            writer.writeParam("pFmt", jw);
        }

        writer.endFunction();

        if (!textField.getExtensionType().isExtended()) {
            writeValueAccessor(writer, textField);
        }
    }

    protected void addClientFormatParams(TextField textField, JsonWriter jw) throws UnifyException {
    	
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

    protected String getFacadeStringValue(TextField textField) throws UnifyException {
        return textField.getStringValue();
    }

    protected String getFacadeHiddenStringValue(TextField textField) throws UnifyException {
        return textField.getStringValue();
    }
    
    private void writeTextField(ResponseWriter writer, TextField textField, String type, ExtensionType extensionType)
            throws UnifyException {
        if (extensionType.isExtended()) {
            writer.write("<div ");
            writeTagId(writer, textField.getBorderId());
            if (!textField.isHiddenMimic()) {
            	writeTagStyleClass(writer, textField);
            }
            
            writeTagStyle(writer, textField);
            writer.write(">");
            writer.write("<div style=\"display:flex;width:100%;\">");

            writeLeadingAddOn(writer, textField);

            if (extensionType.isFacadeHidden()) {
                writer.write("<input type=\"hidden\"");
                writeTagId(writer, textField);
                writeTagName(writer, textField);

                String value = getFacadeHiddenStringValue(textField);
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
                if (extensionType.isFacadeStringValue()) {
                    value = getFacadeStringValue(textField);
                }
            } else {
                writeTagId(writer, textField);
                writeTagName(writer, textField);
                value = textField.getStringValue();
            }

            if (textField.isHiddenMimic()) {
            	writeTagStyleClass(writer, textField);
            } else {
            	writeTagStyleClass(writer, textField.getExtStyleClass());
            }
            
            if (!extensionType.isEdit() && textField.getExtReadOnly()) {
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
        }

        if (value != null) {
            writer.write(" value=\"");
            writer.writeWithHtmlEscape(value);
            writer.write("\"");
        } else if (textField.isHiddenMimic()) {
            writer.write(" value=\"\"");
        }

        writer.write(" spellcheck=\"").write(textField.isSpellCheck()).write("\"");
        if (textField.isAutoComplete()) {
            writer.write(" autocomplete=\"on\"");
        } else {
            writer.write(" autocomplete=\"nef\"");
        }
        
        if (textField.getTabIndex() >= 0) {
            writer.write(" tabindex=\"").write(textField.getTabIndex()).write("\"");
        }
        writer.write("/>");
    }
}
