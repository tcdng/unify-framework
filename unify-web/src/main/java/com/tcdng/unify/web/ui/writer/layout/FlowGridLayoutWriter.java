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

package com.tcdng.unify.web.ui.writer.layout;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.Container;
import com.tcdng.unify.web.ui.Layout;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.layout.FlowGridLayout;
import com.tcdng.unify.web.ui.writer.AbstractLayoutWriter;

/**
 * Flow grid layout writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(FlowGridLayout.class)
@Component("flowgridlayout-writer")
public class FlowGridLayoutWriter extends AbstractLayoutWriter {

    @Override
    public void writeStructureAndContent(ResponseWriter writer, Layout layout, Container container)
            throws UnifyException {
        FlowGridLayout flowGridLayout = (FlowGridLayout) layout;
        writer.write("<div");
        writeTagStyleClass(writer, flowGridLayout.getStyleClass());
        String style =
                "grid-template-columns:repeat(auto-fill, minmax(" + flowGridLayout.getMinColumnWidth() + ", 1fr));";
        if (!StringUtils.isBlank(flowGridLayout.getStyle())) {
            style += flowGridLayout.getStyle();
        }
        writeTagStyle(writer, style);
        writer.write(">");

        for (String longName : container.getLayoutWidgetLongNames()) {
            Widget widget = container.getWidgetByLongName(longName);
            if (widget.isVisible()) {
                writer.write("<div class=\"fgcell\">");
                // Caption
                if (flowGridLayout.isShowCaption()) {
                    String caption = null;
                    boolean isCaption = widget.isLayoutCaption();
                    if (isCaption) {
                        caption = widget.getCaption();
                        isCaption = StringUtils.isNotBlank(caption);
                    }

                    if (isCaption) {
                        String captionCls = "llabel";
                        if (layout.isInlineCaption()) {
                            captionCls = "llabelinl";
                        }

                        writer.write("<div");
                        writeTagStyleClass(writer, captionCls);
                        writeTagStyle(writer, layout.getCaptionStyle());
                        writer.write(">");
                        writer.write(caption);
                        writer.writeNotNull(layout.getCaptionSuffix());
                        writer.write("</div>");
                    } else {
                        writer.write("<div class=\"llabelblank\"></div>");
                    }
                }

                // Content
                String contentCls = "lcontent";
                if (layout.isInlineCaption()) {
                    contentCls = "lcontentinl";
                }
                writer.write("<div class=\"").write(contentCls).write("\">");
                writer.writeStructureAndContent(widget);
                writer.write("</div>");
                writer.write("</div>");
            } else if (widget.isHidden()) {
                writer.writeStructureAndContent(widget);
            }
        }

        writer.write("</div>");
    }

}
