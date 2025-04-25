/*
 * Copyright 2018-2025 The Code Department.
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

import java.util.List;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.PushType;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.CheckList;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Checklist writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Writes(CheckList.class)
@Component("checklist-writer")
public class CheckListWriter extends AbstractControlWriter {

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        CheckList checkList = (CheckList) widget;
        writeHiddenPush(writer, checkList, PushType.CHECKBOX);

        List<? extends Listable> listableList = checkList.getListables();
        int len = listableList.size();
        boolean appendSym = !checkList.getUplAttribute(boolean.class, "flow");
        boolean isContainerDisabled = checkList.isContainerDisabled();
        String baseId = checkList.getId();
        String baseFacId = checkList.getFacadeId();
        for (int i = 0, breaks = len; i < len; i++) {
            Listable listable = listableList.get(i);
            writer.write("<span ");
            writeTagId(writer, baseFacId + i);
            if (isContainerDisabled) {
                writeTagVisualAttributesWithTrailingExtraStyleClass(writer, checkList, "g_cbd");
            } else {
                writeTagVisualAttributesWithTrailingExtraStyleClass(writer, checkList, "g_cbb");
            }

            writer.write("/>");
            writer.write("<input type=\"checkbox\"");
            writeTagId(writer, baseId + i);
            writeTagName(writer, checkList);
            writer.write(" value=\"").write(listable.getListKey()).write("\"/>");
            writer.write("</span>");

            writer.writeWithHtmlEscape(listable.getListDescription());
            if (appendSym && ((--breaks) > 0)) {
                writer.write("<br />");
            }
        }
    }

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);
		CheckList checkList = (CheckList) widget;
		writer.beginFunction("ux.rigChecklist");
		writer.writeParam("pId", checkList.getId());
		writer.writeParam("pNm", checkList.getGroupId());
		writer.writeParam("pVal", checkList.getValue(String[].class));
		writer.writeParam("pActive", checkList.isActive());
		writer.endFunction();
	}
}
