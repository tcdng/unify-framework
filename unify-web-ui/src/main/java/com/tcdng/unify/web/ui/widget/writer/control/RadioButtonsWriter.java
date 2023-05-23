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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.web.ui.widget.PushType;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.RadioButtons;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Radio buttons writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(RadioButtons.class)
@Component("radiobuttons-writer")
public class RadioButtonsWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        RadioButtons radioButtons = (RadioButtons) widget;
        writeHiddenPush(writer, radioButtons, PushType.RADIO);

        List<? extends Listable> listableList = radioButtons.getListables();
        final boolean disabled = radioButtons.isContainerDisabled();
        final int columns = radioButtons.getColumns();
        if (columns > 0) {
            writer.write("<div style=\"display:table;\">");

        	final int len = listableList.size();
        	final int rows = len / columns + (len % columns > 0 ? 1: 0);
        	int i = 0;
        	for (int j = 0; j < rows; j++) {
                writer.write("<div style=\"display:table-row;\">");
        		int k = 0;
        		for(; k < columns && i < len; k++, i++) {
                    writer.write("<div style=\"display:table-cell;\">");
                    writer.write("<input type=\"radio\"");
                    writeTagName(writer, radioButtons);
                    writeTagStyleClass(writer, radioButtons);
                    writeTagStyle(writer, radioButtons);
                    Listable listable = listableList.get(i);
                    writer.write(" value=\"").write(listable.getListKey()).write("\"");
                    if (disabled) {
                        writer.write(" disabled ");
                    }
                    writer.write("/>");
                    writer.writeWithHtmlEscape(listable.getListDescription());
                    writer.write("</div>");        			
        		}
        		
           		while(k < columns) {
           			writer.write("<div style=\"display:table-cell;\"></div>");        			
           			k++;
           		}
        		
                writer.write("</div>");
        	}
        	
            writer.write("</div>");
        } else {
            final boolean isNotFlow = !radioButtons.getUplAttribute(boolean.class, "flow");
            int breaks = listableList.size();
            for (Listable listable : listableList) {
                writer.write("<input type=\"radio\"");
                writeTagName(writer, radioButtons);
                writeTagStyleClass(writer, radioButtons);
                writeTagStyle(writer, radioButtons);
                writer.write(" value=\"").write(listable.getListKey()).write("\"");
                if (disabled) {
                    writer.write(" disabled ");
                }
                writer.write("/>");
                writer.writeWithHtmlEscape(listable.getListDescription());

                if (isNotFlow) {
                    if ((--breaks) > 0) {
                        writer.write("<br />");
                    }
                }
            }
        }
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);
        RadioButtons radioButtons = (RadioButtons) widget;
        writer.beginFunction("ux.rigRadioButtons");
        writer.writeParam("pId", radioButtons.getId());
        writer.writeParam("pNm", radioButtons.getGroupId());
        writer.writeParam("pVal", radioButtons.getValue(String.class));
        writer.endFunction();
    }

}
