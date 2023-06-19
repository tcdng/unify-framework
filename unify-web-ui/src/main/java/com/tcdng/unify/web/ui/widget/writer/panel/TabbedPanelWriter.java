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
package com.tcdng.unify.web.ui.widget.writer.panel;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.panel.TabbedPanel;
import com.tcdng.unify.web.ui.widget.writer.AbstractSwitchPanelWriter;

/**
 * Tabbed panel writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(TabbedPanel.class)
@Component("tabbedpanel-writer")
public class TabbedPanelWriter extends AbstractSwitchPanelWriter {

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);
		TabbedPanel tabbedPanel = (TabbedPanel) widget;
		writer.beginFunction("ux.rigTabbedPanel");
		writer.writeParam("pId", tabbedPanel.getId());
		writer.writeParam("pContId", tabbedPanel.getId());
		writer.writeCommandURLParam("pCmdURL");
		writer.writeParam("pActTabId", tabbedPanel.getActiveTabId());
		writer.writeParam("pActTabIdList", DataUtils.toArray(String.class, tabbedPanel.getActiveTabExpandedIdList()));
		writer.writeParam("pSelTabId", tabbedPanel.getSelectedTabId());
		writer.writeParam("pTabIdList", DataUtils.toArray(String.class, tabbedPanel.getTabIds()));
		List<String> captionIds = new ArrayList<String>();
		for (String longName : tabbedPanel.getLayoutWidgetLongNames()) {
			Widget tabWidget = tabbedPanel.getWidgetByLongName(longName);
			if (!tabWidget.isHidden() && tabWidget.isVisible()) {
				captionIds.add(tabWidget.getPrefixedId("cap_"));
			}
		}
		writer.writeParam("pTabCapIdList", DataUtils.toArray(String.class, captionIds));
		writer.endFunction();
	}

    @Override
    protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
        TabbedPanel tabbedPanel = (TabbedPanel) container;
        writer.writeStructureAndContent(tabbedPanel.getSelectedTabIdCtrl());

        switch (tabbedPanel.getTabPosition()) {
            case BOTTOM:
                writer.write("<div class=\"tbottom\">");
                writer.write("<div style=\"display:table-row;height:100%;\"><div style=\"display:table-cell;\">");
                // Append content
                writeContent(writer, tabbedPanel);
                writer.write("</div></div>");
                writer.write("<div style=\"display:table-row;\"><div style=\"display:table-cell;\">");
                // Append tabs
                writeTabs(writer, tabbedPanel);
                writer.write("</div></div>");
                writer.write("</div>");
                break;
            case LEFT:
                writer.write("<div class=\"tleft\">");
                writer.write(
                        "<div style=\"display:table-row;\"><div style=\"display:table-cell;height:100%;vertical-align:top;\">");
                // Append tabs
                writeTabs(writer, tabbedPanel);
                writer.write("</div>");
                writer.write("<div style=\"display:table-cell;width:100%;height:100%;\">");
                // Append content
                writeContent(writer, tabbedPanel);
                writer.write("</div></div>");
                writer.write("</div>");
                break;
            case RIGHT:
                writer.write("<div class=\"tright\">");
                writer.write(
                        "<div style=\"display:table-row;\"><div style=\"display:table-cell;width:100%;height:100%;\">");
                // Append content
                writeContent(writer, tabbedPanel);
                writer.write("</div>");
                writer.write("<div style=\"display:table-cell;height:100%;vertical-align:top;\">");
                // Append tabs
                writeTabs(writer, tabbedPanel);
                writer.write("</div></div>");
                writer.write("</div>");
                break;
            case TOP:
            default:
                writer.write("<div class=\"ttop\">");
                writer.write("<div style=\"display:table-row;\"><div style=\"display:table-cell;\">");
                // Append tabs
                writeTabs(writer, tabbedPanel);
                writer.write("</div></div>");
                writer.write("<div style=\"display:table-row;height:100%;\"><div style=\"display:table-cell;\">");
                // Append content
                writeContent(writer, tabbedPanel);
                writer.write("</div></div>");
                writer.write("</div>");
                break;
        }
    }

    private void writeTabs(ResponseWriter writer, TabbedPanel tabbedPanel) throws UnifyException {
        Widget currentWidget = tabbedPanel.getCurrentWidget();
        writer.write("<ul class=\"ttab\">");
        for (String longName : tabbedPanel.getLayoutWidgetLongNames()) {
            Widget widget = tabbedPanel.getWidgetByLongName(longName);
            if (!widget.isHidden() && widget.isVisible()) {
                writer.write("<li id=\"").write(widget.getPrefixedId("cap_")).write("\"");
                if (widget == currentWidget) {
                    writer.write("class=\"tactive\"");
                } else {
                    writer.write("class=\"tinactive\"");
                }
                writer.write("><a>");
                writeCaption(writer, widget);
                writer.write("</a></li>");
            }
        }
        writer.write("</ul>");
    }

    private void writeContent(ResponseWriter writer, TabbedPanel tabbedPanel) throws UnifyException {
        Widget currentWidget = tabbedPanel.getCurrentWidget();
        writer.write("<div class=\"tcontent\">");
        currentWidget = tabbedPanel.getCurrentWidget();
        if (currentWidget != null && currentWidget.isVisible()) {
            writer.writeStructureAndContent(currentWidget);
        }
        writer.write("</div>");
    }
}
