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

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.web.ui.ListControlJsonData;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.MultiSelect;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;

/**
 * Multi-select writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(MultiSelect.class)
@Component("multiselect-writer")
public class MultiSelectWriter extends AbstractControlWriter {

    @SuppressWarnings("unchecked")
    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        MultiSelect multiSelect = (MultiSelect) widget;
        writer.write("<div ");
        writeTagStyleClass(writer, multiSelect);
        writeTagStyle(writer, multiSelect);
        writeTagTitle(writer, multiSelect);
        writer.write("><div id=\"").write(multiSelect.getFramePanelId())
                .write("\" style=\"width:100%;height:100%;overflow-y:scroll;overflow-x:hidden;\" tabindex=\"0\">");

        List<String> values = multiSelect.getValue(ArrayList.class, String.class);
        List<? extends Listable> listableList = multiSelect.getListables();
        int length = listableList.size();
        writer.write("<div id=\"").write(multiSelect.getListPanelId()).write("\" class=\"mslist\">");
        String selStyleClass = getUserColorStyleClass("sel");
        for (int i = 0; i < length; i++) {
            Listable listable = listableList.get(i);
            String key = listable.getListKey();
            writer.write("<a");
            writeTagId(writer, multiSelect.getNamingIndexedId(i));
            if (values != null && values.contains(key)) {
                writeTagStyleClass(writer, selStyleClass);
            } else {
                writeTagStyleClass(writer, "norm");
            }
            writer.write("\">");
            writer.writeWithHtmlEscape(listable.getListDescription());
            writer.write("</a>");
        }
        writer.write("</div>");

        writer.write("<select ");
        writeTagId(writer, multiSelect);
        writeTagStyle(writer, "display:none;");
        writer.write(" multiple=\"multiple\">");

        for (Listable listable : multiSelect.getListables()) {
            String key = listable.getListKey();
            writer.write("<option value=\"").write(key).write("\"");
            if (values != null && values.contains(key)) {
                writer.write(" selected");
            }
            writer.write("></option>");
        }
        writer.write("</select>");

        writer.write("</div>");
        writer.write("</div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);

        MultiSelect multiSelect = (MultiSelect) widget;
        String pageName = multiSelect.getId();
        ListControlJsonData listControlJsonData = multiSelect.getListControlJsonData(true, false, false);

        // Append rigging
        writer.beginFunction("ux.rigMultiSelect");
        writer.writeParam("pId", pageName);
        writer.writeParam("pFrmId", multiSelect.getFramePanelId());
        writer.writeParam("pLstId", multiSelect.getListPanelId());
        writer.writeParam("pICnt", listControlJsonData.getSize());
        writer.writeResolvedParam("pLabelIds", listControlJsonData.getJsonSelectIds());
        writer.writeParam("pNormCls", "norm");
        writer.writeParam("pSelCls", getUserColorStyleClass("sel"));
        writer.endFunction();
    }

}
