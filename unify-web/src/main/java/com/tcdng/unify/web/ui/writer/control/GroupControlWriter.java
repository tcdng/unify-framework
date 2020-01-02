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

package com.tcdng.unify.web.ui.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.web.ui.AbstractMultiControl;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.PushType;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.GroupControl;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Group control writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
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
        
        for (AbstractMultiControl.ChildControlInfo childControlInfo : groupControl.getChildControlInfos()) {
            if (childControlInfo.isExternal()) {
                Control control = childControlInfo.getControl();
                control.setGroupId(dataGroupId);
                if (control.isVisible()) {
                    if (space) {
                        if (appendSym) {
                            writer.writeHtmlFixedSpace();
                        } else {
                            appendSym = true;
                        }
                    }
                    control.setValueStore(valueStore);
                    writer.writeStructureAndContent(control);
                }
            }
        }

        if (dataGroupId != null) {
            writeHiddenPush(writer, dataGroupId, PushType.GROUP);
        }
        writer.write("</div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        GroupControl groupControl = (GroupControl) widget;
        super.doWriteBehavior(writer, groupControl);
        ValueStore valueStore = groupControl.getValueStore();
        for (AbstractMultiControl.ChildControlInfo childControlInfo : groupControl.getChildControlInfos()) {
            if (childControlInfo.isExternal()) {
                Control control = childControlInfo.getControl();
                if (control.isVisible() || control.isHidden()) {
                    control.setValueStore(valueStore);
                    writer.writeBehaviour(control);
                }
            }
        }
    }

}
