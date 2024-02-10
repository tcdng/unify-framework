/*
 * Copyright 2018-2024 The Code Department.
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
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.Layout;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.TabularCellType;
import com.tcdng.unify.web.ui.widget.TabularLayout;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.writer.AbstractLayoutWriter;

/**
 * Abstract base class for UI tabular layout writers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractTabularLayoutWriter extends AbstractLayoutWriter {

    @Override
    public void writeStructureAndContent(ResponseWriter writer, Layout layout, Container container)
            throws UnifyException {
        TabularLayout tabularLayout = (TabularLayout) layout;
        writer.write("<div");
        writeTagStyleClass(writer, "ui-tabular " + layout.getStyleClass());
        writeTagStyle(writer, layout.getStyle());
        writer.write(">");
        if (container.isRepeater()) {
            if (container.getRepeatValueStores() != null) {
                writeRepeatTableContent(writer, tabularLayout, container);
            }
        } else {
            writeTableContent(writer, tabularLayout, container);
        }
        writer.write("</div>");
    }

    protected void appendRowStart(ResponseWriter writer, TabularLayout layout, int rowIndex) throws UnifyException {
        writer.write("<div class=\"lrow\">");
    }

    protected void appendRowEnd(ResponseWriter writer) throws UnifyException {
        writer.write("</div>");
    }

    protected void appendCellContent(ResponseWriter writer, TabularLayout layout, Widget widget, int rowIndex,
            int columnIndex) throws UnifyException {
        // Cell
        TabularCellType cellType = layout.getCellType();
        String cellCls = cellType.styleClass();
        if (layout.isCellPadding()) {
            cellCls = cellType.padStyleClass();
        }

        writer.write("<div class=\"").write(cellCls).write("\"");
        boolean isStyle = false;
        String cellStyle = layout.getCellStyle();
        if (cellStyle != null) {
            isStyle = true;
            writer.write(" style=\"").write(cellStyle);
        }
        
        boolean isCaptionVisible = layout.isShowCaption();
        if (columnIndex == 0) {
            String[] heights = layout.getHeights();
            if (heights != null) {
                if (!"none".equals(heights[rowIndex])) {
                    if (!isStyle) {
                        writer.write(" style=\"");
                        isStyle = true;
                    }
                    writer.write("height:").write(heights[rowIndex]).write(";");
                }
            }
        }

        String[] widths = layout.getWidths();
        if (widths != null) {
            if (!"none".equals(widths[columnIndex])) {
                if (!isStyle) {
                    writer.write(" style=\"");
                    isStyle = true;
                }
                writer.write("width:").write(widths[columnIndex]).write(";");
            }
        }

        if (isStyle) {
            writer.write("\">");
        } else {
            writer.write(">");
        }

        // Caption
        if (isCaptionVisible) {
            String caption = null;
            boolean isCaption = widget.isLayoutCaption();
            if (isCaption) {
                caption = widget.getCaption();
                isCaption = StringUtils.isNotBlank(caption);
            }

            if (isCaption) {
                String captionCls = "llabel";
                if (layout.isInlineCaption()) {
                    String inlineCapClass = layout.getInlineCaptionClass();
                    captionCls = inlineCapClass == null ? "llabelinl" : "llabelinl " + inlineCapClass;
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
    }

    protected abstract void writeTableContent(ResponseWriter writer, TabularLayout layout, Container container)
            throws UnifyException;

    protected abstract void writeRepeatTableContent(ResponseWriter writer, TabularLayout layout, Container container)
            throws UnifyException;
}
