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
package com.tcdng.unify.web.ui.widget.writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.PushType;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * Abstract base class for control writers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractControlWriter extends AbstractWidgetWriter implements ControlWriter {

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers) throws UnifyException {
        super.doWriteBehavior(writer, widget, handlers);
        Control control = (Control) widget;
        if (control.isFocus()) {
            if (widget.isUseFacade()) {
                getRequestContextUtil().setFocusOnWidgetId(control.getFacadeId());
            } else {
                getRequestContextUtil().setFocusOnWidgetId(control.getId());
            }
        }

        if (widget.isNoPush()) {
            getRequestContextUtil().addNoPushWidgetId(control.getId());
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

    protected void writeTargetHidden(ResponseWriter writer, String id, Object value) throws UnifyException {
        writer.write("<input type=\"hidden\"");
        writeTagId(writer, "trg_" + id);
        writeTagValue(writer, value);
        writer.write("/>");
    }

    protected void writeButton(ResponseWriter writer, String id, String styleClass, String style, String caption)
            throws UnifyException {
        writer.write("<button type=\"button\"");
        writeTagId(writer, id);
        if (styleClass != null) {
            writeTagStyleClass(writer, styleClass);
        }

        if (style != null) {
            writeTagStyle(writer, style);
        }

        writer.write(">");
        writer.writeWithHtmlEscape(caption);
        writer.write("</button>");
    }

    protected void writeValueAccessor(ResponseWriter writer, Widget widget) throws UnifyException {
        writer.beginFunction("ux.rigValueAccessor");
        writer.writeParam("uId", widget.getId());
        writer.endFunction();
    }
    
    protected void addPageAlias(String parentPageName, Widget widget) throws UnifyException {
        getRequestContextUtil().addPageAlias(parentPageName, widget.getId());
    }
}
