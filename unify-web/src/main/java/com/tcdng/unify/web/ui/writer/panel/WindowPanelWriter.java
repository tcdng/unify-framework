/*
 * Copyright 2018 The Code Department
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
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.panel.WindowPanel;
import com.tcdng.unify.web.ui.writer.AbstractPanelWriter;

/**
 * Window panel writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(WindowPanel.class)
@Component("windowpanel-writer")
public class WindowPanelWriter extends AbstractPanelWriter {

    @Override
    protected void doWriteInnerStructureAndContent(ResponseWriter writer, Panel panel) throws UnifyException {
        WindowPanel windowPanel = (WindowPanel) panel;
        writer.write("<div style=\"display:table;width:100%;height:100%;\">");
        writer.write("<div style=\"display:table-row;\"><div style=\"display:table-cell;\">");
        writer.write("<div class=\"wptitle\">");
        writeCaption(writer, windowPanel);
        writer.write("</div>");
        writer.write("</div></div>");
        writer.write("<div style=\"display:table-row;\"><div style=\"display:table-cell;height:100%;\">");
        writeLayoutContent(writer, windowPanel);
        writer.write("</div></div>");
        writer.write("</div>");
    }
}
