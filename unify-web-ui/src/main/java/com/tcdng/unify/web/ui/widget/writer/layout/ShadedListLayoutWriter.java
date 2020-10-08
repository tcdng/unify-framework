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

package com.tcdng.unify.web.ui.widget.writer.layout;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.Layout;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.layout.ShadedListLayout;
import com.tcdng.unify.web.ui.widget.writer.AbstractLayoutWriter;

/**
 * Shaded list layout writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(ShadedListLayout.class)
@Component("shadedlistlayout-writer")
public class ShadedListLayoutWriter extends AbstractLayoutWriter {

    @Override
    public void writeStructureAndContent(ResponseWriter writer, Layout layout, Container container)
            throws UnifyException {
        ShadedListLayout shadedListLayout = (ShadedListLayout) layout;
        writer.write("<table");
        writeTagStyleClass(writer, shadedListLayout.getStyleClass());
        writeTagStyle(writer, shadedListLayout.getStyle());
        writer.write(">");
        boolean isAlternate = container.isAlternate();
        if (shadedListLayout.isInlineMode()) {
            writer.write("<tr>");
            int colWidth = 100;
            int minColumns = shadedListLayout.minColumns();
            int itemCount = container.getLayoutWidgetLongNames().size();
            if (itemCount < minColumns) {
                itemCount = minColumns;
            }

            if (itemCount > 0) {
                colWidth = colWidth / itemCount;
            }

            for (String longName : container.getLayoutWidgetLongNames()) {
                Widget widget = container.getWidgetByLongName(longName);
                writer.write("<td class=\"islbase");
                writeLayoutColor(writer, widget);
                writer.write("\" style=\"width:").write(colWidth).write("%;\">");
                if (widget.isVisible()) {
                    widget.setAlternateMode(isAlternate);
                    writer.write("<span class=\"islcap\">");
                    String caption = widget.getCaption();
                    if (StringUtils.isNotBlank(caption)) {
                        writer.write(caption);
                        if (caption.charAt(caption.length() - 1) != '?') {
                            writer.writeNotNull(shadedListLayout.getCaptionSuffix());
                        }
                    }
                    writer.write("</span>");
                    writer.writeStructureAndContent(widget);
                }
                writer.write("</td>");
                minColumns--;
            }

            while ((--minColumns) >= 0) {
                writer.write("<td class=\"islbase\" style=\"width:").write(colWidth).write("%;\"></td>");
            }
            writer.write("</tr>");
        } else {
            for (String longName : container.getLayoutWidgetLongNames()) {
                Widget widget = container.getWidgetByLongName(longName);
                if (widget.isVisible()) {
                    widget.setAlternateMode(isAlternate);
                    writer.write("<tr>");
                    if (shadedListLayout.isShowCaption()) {
                        writer.write("<td class=\"slcap\">");
                        if (StringUtils.isNotBlank(widget.getCaption())) {
                            writer.write(widget.getCaption());
                            writer.writeNotNull(shadedListLayout.getCaptionSuffix());
                        }
                        writer.write("</td>");
                    }

                    writer.write("<td class=\"slcnt");
                    writeLayoutColor(writer, widget);
                    writer.write("\">");
                    writer.writeStructureAndContent(widget);
                    writer.write("</td>");
                    writer.write("</tr>");
                }
            }
        }
        writer.write("</table>");
    }

    private void writeLayoutColor(ResponseWriter writer, Widget widget) throws UnifyException {
        if (widget instanceof Control) {
            Control control = (Control) widget;
            if (control.isLayoutColorMode()) {
                switch (control.getColorMode()) {
                    case ERROR:
                        writer.write(" err");
                        break;
                    case GRAYED:
                        writer.write(" gray");
                        break;
                    case OK:
                        writer.write(" ok");
                        break;
                    case WARNING:
                        writer.write(" warn");
                        break;
                    case NORMAL:
                    default:
                        break;
                }
            }
        }

    }
}
