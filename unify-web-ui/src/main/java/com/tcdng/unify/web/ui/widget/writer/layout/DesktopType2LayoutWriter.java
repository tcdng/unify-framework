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
package com.tcdng.unify.web.ui.widget.writer.layout;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.Document;
import com.tcdng.unify.web.ui.widget.DocumentLayout;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.layout.DesktopType2Layout;

/**
 * Type-2 desktop layout writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(DesktopType2Layout.class)
@Component("desktoptype2-writer")
public class DesktopType2LayoutWriter extends AbstractDocumentLayoutWriter {

    @Override
    public void writeBehaviour(ResponseWriter writer, DocumentLayout layout, Document document) throws UnifyException {
        boolean isCollapsible = layout.getUplAttribute(boolean.class, "collapsibleSidebar");
        if (isCollapsible) {
            String docPageName = document.getId();
            writer.beginFunction("ux.rigDesktopType2");
            writer.writeParam("pId", docPageName);
            writer.writeParam("pMenuId", docPageName + "_menu");
            writer.writeParam("pGripId", docPageName + "_grip");
            writer.writeParam("pOpen", true);
            writer.endFunction();
        }
    }

    @Override
    protected void writeInnerStructureAndContent(ResponseWriter writer, DocumentLayout layout, Document document)
            throws UnifyException {
        boolean isCollapsible = layout.getUplAttribute(boolean.class, "collapsibleSidebar");
        String docPageName = document.getId();
        writeSection(writer, "header", document.getHeaderPanel());
        writeLatencySection(writer, document);
        writer.write("<div class=\"midSection\">");
        writer.write("<div class=\"cell\">");
        writer.write("<div style=\"display:table; width:100%; height:100%\">");
        writer.write("<div style=\"display:table-row; width:100%;\">");
        writer.write("<div id=\"").write(docPageName).write("_menu\" class=\"menuopen\">");
        if (document.getMenuPanel() != null) {
            writer.writeStructureAndContent(document.getMenuPanel());
        }
        writer.write("</div>");
        if (isCollapsible) {
            writer.write("<div class=\"collapser\">");
            writer.write("<span id=\"").write(docPageName).write("_grip\" class=\"grip\"/>");
            writer.write("</div>");
        }
        writer.write("<div class=\"content\">");
        writer.writeStructureAndContent(document.getContentPanel());
        writer.write("</div>");
        writer.write("</div>");
        writer.write("</div>");
        writer.write("</div>");
        writer.write("</div>");
        writeSection(writer, "footer", document.getFooterPanel());
    }

}
