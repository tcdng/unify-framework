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
package com.tcdng.unify.web.ui.writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.Container;
import com.tcdng.unify.web.ui.Layout;
import com.tcdng.unify.web.ui.PushType;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;

/**
 * Abstract base class for UI container writers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractContainerWriter extends AbstractWidgetWriter implements ContainerWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        Container container = (Container) widget;
        writer.write("<div");
        writeTagAttributes(writer, container);
        writer.write(">");
        writeLayoutContent(writer, container);
        writer.write("</div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        Container container = (Container) widget;
        super.doWriteBehavior(writer, container);
        writeContainedWidgetsBehavior(writer, container);
    }

    protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
        Layout layout = container.getLayout();
        if (container.isUseLayoutIfPresent() && layout != null) {
            writer.writeStructureAndContent(layout, container);
        } else {
            boolean isSpace = container.isSpace();
            for (String longName : container.getLayoutWidgetLongNames()) {
                Widget widget = container.getWidgetByLongName(longName);
                if (widget.isVisible()) {
                    writer.writeStructureAndContent(widget);
                    if (isSpace) {
                        writer.writeHtmlFixedSpace();
                    }
                } else if (widget.isHidden()) {
                    writer.writeStructureAndContent(widget);
                }
            }
        }
    }

    protected void writeContainedWidgetsBehavior(ResponseWriter writer, Container container) throws UnifyException {
        for (String longName : container.getLayoutWidgetLongNames()) {
            Widget widget = container.getWidgetByLongName(longName);
            if (widget.isVisible() || widget.isHidden() || widget.isBehaviorAlways()) {
                writer.writeBehaviour(widget);
            }
        }
    }

    protected void writeHiddenPush(ResponseWriter writer, Widget widget, PushType type) throws UnifyException {
        writeHidden(writer, widget.getId(), type.getPrefix());
    }

    protected void writeHiddenPush(ResponseWriter writer, String id, PushType type) throws UnifyException {
        writeHidden(writer, id, type.getPrefix());
    }

    protected void writeHidden(ResponseWriter writer, String id, Object value) throws UnifyException {
        writer.write("<input type=\"hidden\"");
        writeTagId(writer, id);
        writeTagValue(writer, value);
        writer.write("/>");
    }
}
