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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.panel.DetachedPanel;
import com.tcdng.unify.web.ui.widget.writer.AbstractPanelWriter;

/**
 * Detached panel writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(DetachedPanel.class)
@Component("detachedpanel-writer")
public class DetachedPanelWriter extends AbstractPanelWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        DetachedPanel detachedPanel = (DetachedPanel) widget;
        writer.write("<div");
        writeTagId(writer, detachedPanel);
        writeTagStyleClass(writer, detachedPanel.getStyleClass());
        writeTagStyle(writer, "display:none;position:fixed;z-index:200;");
        writer.write(">");
        writeInnerStructureAndContent(writer, detachedPanel);
        writer.write("</div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers) throws UnifyException {
        super.doWriteBehavior(writer, widget, handlers);
        DetachedPanel detachedPanel = (DetachedPanel) widget;

        // Append detached panel rigging
        writer.beginFunction("ux.rigDetachedPanel");
        writer.writeParam("pId", detachedPanel.getId());
        writer.writeParam("pOrient", detachedPanel.getOrientation().toString().toLowerCase());
        writer.endFunction();
    }

}
