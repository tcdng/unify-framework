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
import com.tcdng.unify.core.constant.ContentTypeConstants;
import com.tcdng.unify.web.ui.Container;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.panel.FixedContentPanel;
import com.tcdng.unify.web.ui.writer.AbstractPanelWriter;

/**
 * Fixed content panel writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(FixedContentPanel.class)
@Component("fixedcontentpanel-writer")
public class FixedContentPanelWriter extends AbstractPanelWriter {

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        FixedContentPanel fixedContentPanel = (FixedContentPanel) widget;
        writer.write("ux.rigFixedContentPanel({");
        writer.write("\"pHintPanelId\":\"").write(fixedContentPanel.getHintPanelId()).write("\"");
        writer.write(",\"pBusyIndId\":\"").write(fixedContentPanel.getBusyIndicatorId()).write("\"");
        writer.write("});");
        
        super.doWriteBehavior(writer, widget);
    }

    @Override
    protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
        FixedContentPanel fixedContentPanel = (FixedContentPanel) container;
        writer.write("<div id=\"").write(fixedContentPanel.getHintPanelId()).write("\" class=\"fcphint\"></div>");
        writer.write("<div id=\"").write(fixedContentPanel.getBusyIndicatorId()).write("\" class=\"fcpbusy\">");
        writer.write("<img class=\"fcpimage\" src=\"");
        writer.writeContextResourceURL("/resource/file", ContentTypeConstants.IMAGE, "$t{images/busy.gif}");
        writer.write("\"></div>");
      
        super.writeLayoutContent(writer, container);
    }

}
