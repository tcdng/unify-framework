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
package com.tcdng.unify.web.ui.widget.writer.panel;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.panel.SplitPanel;
import com.tcdng.unify.web.ui.widget.writer.AbstractPanelWriter;

/**
 * Split panel writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(SplitPanel.class)
@Component("splitpanel-writer")
public class SplitPanelWriter extends AbstractPanelWriter {

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		SplitPanel splitPanel = (SplitPanel) widget;
		List<String> longNames = splitPanel.getLayoutWidgetLongNames();
		for (int i = 0; i < 2; i++) {
			Widget innerWidget = splitPanel.getWidgetByLongName(longNames.get(i));
			if (innerWidget.isVisible() || innerWidget.isHidden()) {
				writer.writeBehavior(innerWidget);
			}
		}
		writer.beginFunction("ux.rigSplitPanel");
		writer.writeParam("pId", splitPanel.getId());
		writer.writeParam("pContId", splitPanel.getId());
		writer.writeCommandURLParam("pCmdURL");
		writer.writeParam("pCtrlId", splitPanel.getSplitCtrlId());
		writer.writeParam("pMinorId", splitPanel.getMinorWinId());
		writer.writeParam("pMinorScrId", splitPanel.getMinorPaneId());
		writer.writeParam("pMajorScrId", splitPanel.getMajorPaneId());
		writer.writeParam("pMax", splitPanel.getMinorWinMax());
		writer.writeParam("pMin", splitPanel.getMinorWinMin());
		writer.writeParam("pVert", splitPanel.isVertical());
		writer.endFunction();
	}

    @Override
    protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
        SplitPanel splitPanel = (SplitPanel) container;
        List<String> longNames = splitPanel.getLayoutWidgetLongNames();
        int minorWidth = splitPanel.getMinorWidth();
        String styleClass = splitPanel.getUplAttribute(String.class, "styleClass");
        writer.write("<div  style=\"display:table; width:100%; height:100%;\">");
        if (splitPanel.isVertical()) {
            writer.write("<div id=\"").write(splitPanel.getMinorWinId())
                    .write("\" style=\"display:table-cell;vertical-align:top;width:").write(minorWidth)
                    .write("px;height:100%;\">");
            writer.write("<div id=\"").write(splitPanel.getMinorPaneId())
                    .write("\" class=\"spminor\" style=\"display:none;width:").write(minorWidth)
                    .write("px;overflow:hidden;\">");
            Widget widget = splitPanel.getWidgetByLongName(longNames.get(0));
            if (widget.isVisible() || widget.isHidden()) {
                writer.writeStructureAndContent(widget);
            }
            writer.write("</div></div>");
            writer.write("<div id=\"").write(splitPanel.getSplitCtrlId())
                    .write("\" style=\"display:table-cell;width:4px;height:100%;cursor:e-resize;\"></div>");
            writer.write("<div style=\"display:table-cell;vertical-align:top;height:100%;\">");
            writer.write("<div id=\"").write(splitPanel.getMajorPaneId())
                    .write("\" class=\"spmajor\" style=\"display:none;width:100%;overflow:hidden;\">");
            widget = splitPanel.getWidgetByLongName(longNames.get(1));
            ;
            if (widget.isVisible() || widget.isHidden()) {
                writer.writeStructureAndContent(widget);
            }
            writer.write("</div></div>");
        } else {
            writer.write("<div id=\"").write(splitPanel.getMinorWinId())
                    .write("\" style=\"display:table-row;vertical-align:top;height:").write(minorWidth).write("px;\">");
            writer.write("<div id=\"").write(splitPanel.getMinorPaneId()).write("\" class=\"").write(styleClass)
                    .write("-minor").write("\" style=\"display:inline-block;height:").write(minorWidth)
                    .write("px;width:100%;overflow:hidden;\">");
            Widget widget = splitPanel.getWidgetByLongName(longNames.get(0));
            if (widget.isVisible() || widget.isHidden()) {
                writer.writeStructureAndContent(widget);
            }
            writer.write("</div></div>");
            writer.write("<div id=\"").write(splitPanel.getSplitCtrlId())
                    .write("\" style=\"display:table-row;height:4px;cursor:n-resize;\"></div>");
            writer.write("<div style=\"display:table-row;vertical-align:top;\">");
            writer.write("<div class=\"").write(styleClass).write("-major")
                    .write("\" style=\"display:inline-block;width:100%;height:100%;overflow:hidden;\">");
            widget = splitPanel.getWidgetByLongName(longNames.get(1));
            ;
            if (widget.isVisible() || widget.isHidden()) {
                writer.writeStructureAndContent(widget);
            }
            writer.write("</div></div>");
        }
        writer.write("</div>");
    }

}
