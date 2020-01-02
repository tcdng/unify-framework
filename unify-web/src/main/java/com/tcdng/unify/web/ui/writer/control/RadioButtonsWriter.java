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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.web.ui.PushType;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.RadioButtons;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Radio buttons writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(RadioButtons.class)
@Component("radiobuttons-writer")
public class RadioButtonsWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        RadioButtons radioButtons = (RadioButtons) widget;
        writeHiddenPush(writer, radioButtons, PushType.RADIO);

        String value = radioButtons.getValue(String.class);
        List<? extends Listable> listableList = radioButtons.getListables();
        boolean isNotFlow = !radioButtons.getUplAttribute(boolean.class, "flow");
        int breaks = listableList.size();
        for (Listable listable : listableList) {
            writer.write("<input type=\"radio\"");
            writeTagName(writer, radioButtons);
            writeTagStyleClass(writer, radioButtons);
            writeTagStyle(writer, radioButtons);
            String key = listable.getListKey();

            if (key.equals(value)) {
                writer.write(" checked");
            }
            writer.write(" value=\"").write(key).write("\"/>");
            writer.writeWithHtmlEscape(listable.getListDescription());

            if (isNotFlow) {
                if ((--breaks) > 0) {
                    writer.write("<br />");
                }
            }
        }
    }

}
