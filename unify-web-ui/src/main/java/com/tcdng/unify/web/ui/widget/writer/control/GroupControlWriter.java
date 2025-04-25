/*
 * Copyright (c) 2018-2025 The Code Department.
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

package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.web.ui.widget.AbstractMultiControl;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.PushType;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.GroupControl;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Grouping control writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Writes(GroupControl.class)
@Component("group-writer")
public class GroupControlWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        GroupControl groupControl = (GroupControl) widget;
        writer.write("<div");
        writeTagAttributes(writer, groupControl);
        writer.write(">");
        ValueStore valueStore = groupControl.getValueStore();
        boolean space = groupControl.isSpace();
        boolean appendSym = false;

        String dataGroupId = null;
        if (groupControl.isContainerEditable()) {
            dataGroupId = groupControl.getDataGroupId();
        }

        for (AbstractMultiControl.ChildWidgetInfo childWidgetInfo : groupControl.getChildWidgetInfos()) {
            if (childWidgetInfo.isExternal()) {
                Widget chWidget = childWidgetInfo.getWidget();
                chWidget.setGroupId(dataGroupId);
                if (chWidget.isVisible()) {
                    if (space) {
                        if (appendSym) {
                            writer.writeHtmlFixedSpace();
                        } else {
                            appendSym = true;
                        }
                    }
                    chWidget.setValueStore(valueStore);
                    writer.writeStructureAndContent(chWidget);
                }
            }
        }

        if (dataGroupId != null) {
            writeHiddenPush(writer, dataGroupId, PushType.GROUP);
        }
        writer.write("</div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers) throws UnifyException {
        GroupControl groupControl = (GroupControl) widget;
        super.doWriteBehavior(writer, groupControl, handlers);
        ValueStore valueStore = groupControl.getValueStore();
        for (AbstractMultiControl.ChildWidgetInfo childWidgetInfo : groupControl.getChildWidgetInfos()) {
            if (childWidgetInfo.isExternal()) {
                Widget chWidget = childWidgetInfo.getWidget();
                if (chWidget.isVisible() || chWidget.isHidden()) {
                    chWidget.setValueStore(valueStore);
                    writer.writeBehavior(chWidget);
                }
            }
        }
    }

}
