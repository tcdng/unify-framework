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
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.ColorLegend;
import com.tcdng.unify.web.ui.widget.data.ColorLegendInfo;
import com.tcdng.unify.web.ui.widget.data.ColorLegendItem;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Color legend writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(ColorLegend.class)
@Component("colorlegend-writer")
public class ColorLegendWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
    	ColorLegend legend = (ColorLegend) widget;
        writer.write("<div ");
        writeTagAttributes(writer, widget);
        writer.write(">");
        writer.write("<div style=\"display:table;width:100%;\">");
        ColorLegendInfo info = legend.getColorLegendInfo();
        if (info != null) {
        	final int len = info.size();
        	final int columns = legend.getColumns() <= 0 ? 1 : legend.getColumns();
        	final int rows = len / columns + (len % columns > 0 ? 1: 0);
        	final int width = 100 / columns;
        	int k = 0;
        	for (int i = 0; i < rows; i++) {
                writer.write("<div style=\"display:table-row;\">");
                for (int j = 0; j < columns; j++, k++) {
                    writer.write("<div style=\"display:table-cell;width:").write(width).write("%;\">");
                	if (k < len) {
                		ColorLegendItem item = info.getItem(k);
                        writer.write("<div style=\"display:table;width:100%;\">");
                        writer.write("<div style=\"display:table-row;\">");
                        writer.write("<div class=\"key\" style=\"display:table-cell;\">");
                        writer.write("<span style=\"background-color:").write(item.getColor()).write(";\">");
                        writer.write("</span>");
                        writer.write("</div>");
                        writer.write("<div class=\"label\" style=\"display:table-cell;\">");
                        writer.writeWithHtmlEscape(item.getLabel());
                        writer.write("</div>");
                        writer.write("</div>");
                        writer.write("</div>");
                	}
                    writer.write("</div>");
                }       		

                writer.write("</div>");
        	}
        }        
        writer.write("</div>");
        writer.write("</div>");
    }
}
