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
package com.tcdng.unify.web.ui.writer.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.Container;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.panel.AccordionPanel;
import com.tcdng.unify.web.ui.writer.AbstractPanelWriter;

/**
 * Accordion panel writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(AccordionPanel.class)
@Component("accordionpanel-writer")
public class AccordionPanelWriter extends AbstractPanelWriter {

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);
        AccordionPanel accordionPanel = (AccordionPanel) widget;
        writer.write("ux.rigAccordion({");
        writer.write("\"pId\":\"").write(accordionPanel.getId()).write('"');
        writer.write(",\"pContId\":\"").write(accordionPanel.getContainerId()).write('"');
        writer.write(",\"pCmdURL\":\"");
        writer.writeCommandURL();
        writer.write('"');
        writer.write(",\"pHeaderIdBase\":\"").write(accordionPanel.getHeaderIdBase()).write('"');
        writer.write(",\"pCollapsed\":").write(accordionPanel.isCollapsed());
        writer.write(",\"pCurrSelCtrlId\":\"").write(accordionPanel.getCurrentSelCtrl().getId()).write('"');
        writer.write(",\"pCurrSelIdx\":").write(accordionPanel.getCurrentSel());
        writer.write(",\"pSectionCount\":").write(accordionPanel.getSectionCount());
        writer.write("});");
    }

    @Override
    protected void doWriteInnerStructureAndContent(ResponseWriter writer, Panel panel) throws UnifyException {
        writeLayoutContent(writer, panel);
    }

    @Override
    protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
        AccordionPanel accordionPanel = (AccordionPanel) container;
        accordionPanel.clearSectionCount();
        for (String longName : accordionPanel.getLayoutWidgetLongNames()) {
            Widget widget = accordionPanel.getWidgetByLongName(longName);
            if (widget.isVisible()) {
                int sectionCount = accordionPanel.getSectionCount();
                boolean isExpand = !accordionPanel.isCollapsed() && sectionCount == accordionPanel.getCurrentSel();
                writer.write("<div id=\"").write(accordionPanel.getHeaderIdBase()).write(sectionCount);
                if (isExpand) {
                    writer.write("\" class=\"aaheader\">");
                } else {
                    writer.write("\" class=\"aheader\">");
                }
                writer.writeWithHtmlEscape(widget.getUplAttribute(String.class, "caption"));
                writer.write("</div>");
                if (isExpand) {
                    writer.write("<div class=\"acontent\">");
                    writer.writeStructureAndContent(widget);
                    writer.write("</div>");
                }
                accordionPanel.incrementSectionCount();
            }
        }
        writer.writeStructureAndContent(accordionPanel.getCurrentSelCtrl());
    }

}
