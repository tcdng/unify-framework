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
package com.tcdng.unify.web.ui.widget.writer.control;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ui.widget.ListControlInfo;
import com.tcdng.unify.web.ui.widget.PushType;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.widget.control.DropdownCheckList;

/**
 * Dropdown multi-select writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(DropdownCheckList.class)
@Component("dropdownchecklist-writer")
public class DropdownCheckListWriter extends AbstractPopupTextFieldWriter {

    @Override
    protected void writePopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        DropdownCheckList dropdownCheckList = (DropdownCheckList) popupTextField;
        List<? extends Listable> listableList = dropdownCheckList.getListables();
        int length = listableList.size();
        if (length > 0) {
            int columns = dropdownCheckList.getColumns();
            if (columns <= 0) {
                columns = 1;
            } else if (columns > length) {
                columns = length;
            }

            boolean isDisabled = dropdownCheckList.isDisabled();
            dropdownCheckList
                    .setDisabled(dropdownCheckList.isContainerDisabled() || !dropdownCheckList.isContainerEditable());

            writeHiddenPush(writer, dropdownCheckList, PushType.CHECKBOX);

            boolean isContainerDisabled = dropdownCheckList.isContainerDisabled();
            writer.write("<div class=\"dclframe\"><table>");
            String selectAllOption = dropdownCheckList.getSelectAllOption();
            if (selectAllOption != null) {
                writer.write("<tr><td colspan=\"").write(columns * 2).write("\">");
                writer.write("<span ");
                writeTagId(writer, "fac_" + dropdownCheckList.getSelectAllId());
                if (isContainerDisabled) {
                    writeTagStyleClass(writer, "g_cbd");
                } else {
                    writeTagStyleClass(writer, "g_cbb");
                }
                writer.write("/>");
                writer.write("<input type=\"checkbox\"");
                writeTagId(writer, dropdownCheckList.getSelectAllId());
                writer.write("/>");
                writer.write("</span>");
                if (StringUtils.isNotBlank(selectAllOption)) {
                    writer.writeWithHtmlEscape(selectAllOption);
                } else {
                    writer.writeHtmlFixedSpace();
                }
                writer.write("</td></tr>");
            }

            String groupId = dropdownCheckList.getId();
            for (int i = 0; i < length;) {
                writer.write("<tr>");
                for (int j = 0; j < columns; j++, i++) {
                    if (i < length) {
                        Listable listable = listableList.get(i);
                        writer.write("<td>");
                        String namingIndexId = dropdownCheckList.getNamingIndexedId(i);
                        writer.write("<span ");
                        writeTagId(writer, "fac_" + namingIndexId);
                        if (isContainerDisabled) {
                            writeTagStyleClass(writer, "g_cbd");
                        } else {
                            writeTagStyleClass(writer, "g_cbb");
                        }

                        writer.write("/>");
                        writer.write("<input type=\"checkbox\"");
                        writeTagId(writer, namingIndexId);
                        writeTagName(writer, groupId);
                        writeTagValue(writer, listable.getListKey());
                        writer.write("/>");
                        writer.write("</span>");
                        writer.write("</td><td>");
                        writer.writeWithHtmlEscape(listable.getListDescription());
                        writer.write("</td>");
                    } else {
                        writer.write("<td>&nbsp;</td><td>&nbsp;</td>");
                    }
                }
                writer.write("</tr>");
            }
            writer.write("</table></div>");
            dropdownCheckList.setDisabled(isDisabled);
        }
    }

    @Override
    protected void doWritePopupTextFieldBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField,
            boolean popupEnabled) throws UnifyException {
        DropdownCheckList dropdownCheckList = (DropdownCheckList) popupTextField;
        ListControlInfo listControlInfo = dropdownCheckList.getListControlInfo(popupTextField.getFormatter());
        writer.beginFunction("ux.rigDropdownChecklist");
        writer.writeParam("pId", dropdownCheckList.getId());
        writer.writeParam("pNm", dropdownCheckList.getId());
        writer.writeParam("pFacId", dropdownCheckList.getFacadeId());
        if (dropdownCheckList.getSelectAllOption() != null) {
            writer.writeParam("pSelAllId", dropdownCheckList.getSelectAllId());
        }

        writer.writeParam("pSelectIds", listControlInfo.getSelectIds());
        writer.writeParam("pKeys", listControlInfo.getKeys());
        writer.writeParam("pLabels", listControlInfo.getLabels());
        writer.writeParam("pVal", dropdownCheckList.getValue(String[].class));
        writer.writeParam("pEnabled", popupEnabled);
        writer.writeParam("pActive", popupEnabled);
        writer.endFunction();
    }

    @Override
    protected String getOnShowAction() throws UnifyException {
        return null;
    }

    @Override
    protected String getOnShowParam(AbstractPopupTextField popupTextField) throws UnifyException {
        return null;
    }

    @Override
    protected String getOnHideAction() throws UnifyException {
        return "ux.dcHidePopup";
    }

    @Override
    protected String getOnHideParam(AbstractPopupTextField popupTextField) throws UnifyException {
        DropdownCheckList dropdownCheckList = (DropdownCheckList) popupTextField;
        JsonWriter jw = new JsonWriter();
        jw.beginObject();
        jw.write("id", dropdownCheckList.getId());
        jw.endObject();
        return jw.toString();
    }

}
