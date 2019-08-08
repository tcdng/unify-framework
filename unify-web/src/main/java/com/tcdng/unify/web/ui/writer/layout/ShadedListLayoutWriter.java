/*
 * Copyright 2018-2019 The Code Department.
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

package com.tcdng.unify.web.ui.writer.layout;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.Container;
import com.tcdng.unify.web.ui.Layout;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.layout.ShadedListLayout;
import com.tcdng.unify.web.ui.writer.AbstractLayoutWriter;

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
        for (String longName : container.getLayoutWidgetLongNames()) {
            Widget widget = container.getWidgetByLongName(longName);
            if (widget.isVisible()) {
                writer.write("<tr>");
                if (shadedListLayout.isShowCaption()) {
                    writer.write("<td class=\"slcap\">");
                    if (!StringUtils.isBlank(widget.getCaption())) {
                        writer.write(widget.getCaption());
                        writer.writeNotNull(shadedListLayout.getCaptionSuffix());
                    }
                    writer.write("</td>");
                }
                
                writer.write("<td class=\"slcnt\">");
                writer.writeStructureAndContent(widget);
                writer.write("</td>");
                writer.write("</tr>");
            }
        }
        writer.write("</table>");
    }

}
