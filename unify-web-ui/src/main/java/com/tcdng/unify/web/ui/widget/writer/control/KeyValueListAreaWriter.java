/*
 * Copyright (c) 2018-2025 The Code Department.
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

import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.KeyValueListArea;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Key-value list area writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Writes(KeyValueListArea.class)
@Component("keyvaluelistarea-writer")
public class KeyValueListAreaWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        KeyValueListArea keyValueListArea = (KeyValueListArea) widget;
        String styleClass = keyValueListArea.getUplAttribute(String.class, "styleClass");
        writer.write("<div ");
        writeTagAttributes(writer, keyValueListArea);
        writer.write("><table class=\"").write(styleClass).write("-body\">");
        Map<Object, Object> map = keyValueListArea.getMapValue();
        if (map != null) {
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                writer.write("<tr><td class=\"").write(styleClass).write("-key\">");
                writer.write(entry.getKey());
                writer.write("</td><td class=\"").write(styleClass).write("-value\">");
                writer.write(entry.getValue());
                writer.write("</td></tr>");
            }
        }
        writer.write("</table></div>");
    }
}
