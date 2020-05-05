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
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.ListControlJsonData;
import com.tcdng.unify.web.ui.PushType;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.control.DropdownCheckList;

/**
 * Dropdown multi-select writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(DropdownCheckList.class)
@Component("dropdownchecklist-writer")
public class DropdownCheckListWriter extends AbstractPopupTextFieldWriter {

    @SuppressWarnings("unchecked")
    @Override
    protected void appendPopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
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

            List<String> values = dropdownCheckList.getValue(ArrayList.class, String.class);
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
                        String key = listable.getListKey();
                        boolean checked = values != null && values.contains(key);
                        String namingIndexId = dropdownCheckList.getNamingIndexedId(i);
                        writer.write("<span ");
                        writeTagId(writer, "fac_" + namingIndexId);
                        if (checked) {
                            if (isContainerDisabled) {
                                writeTagStyleClass(writer, "g_cbc");
                            } else {
                                writeTagStyleClass(writer, "g_cba");
                            }
                        } else {
                            if (isContainerDisabled) {
                                writeTagStyleClass(writer, "g_cbd");
                            } else {
                                writeTagStyleClass(writer, "g_cbb");
                            }
                        }

                        writer.write("/>");
                        writer.write("<input type=\"checkbox\"");
                        writeTagId(writer, namingIndexId);
                        writeTagName(writer, groupId);
                        if (checked) {
                            writer.write(" checked=\"checked\"");
                        }

                        writeTagValue(writer, key);
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
    protected void appendPopupBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        DropdownCheckList dropdownCheckList = (DropdownCheckList) popupTextField;
        if (dropdownCheckList.isContainerEditable() && !dropdownCheckList.isContainerDisabled()) {
            writer.write("ux.rigDropdownChecklist({");
            writer.write("\"pId\":\"").write(dropdownCheckList.getId()).write('"');
            writer.write(",\"pNm\":\"").write(dropdownCheckList.getId()).write('"');
            if (dropdownCheckList.getSelectAllOption() != null) {
                writer.write(",\"pSelAllId\":\"").write(dropdownCheckList.getSelectAllId()).write('"');
            }
            writer.write("});");
        }
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
        return "setcheckedpatternvalue";
    }

    @Override
    protected String getOnHideParam(AbstractPopupTextField popupTextField) throws UnifyException {
        DropdownCheckList dropdownCheckList = (DropdownCheckList) popupTextField;
        ListControlJsonData listControlData = dropdownCheckList.getListControlJsonData(true, false, true);
        StringBuilder psb = new StringBuilder();
        psb.append("{\"id\":\"").append(dropdownCheckList.getId()).append("\"");
        psb.append(",\"fillId\":\"").append(dropdownCheckList.getFacadeId()).append("\"");
        psb.append(",\"chkIds\":").append(listControlData.getJsonSelectIds());
        psb.append(",\"fillValues\":").append(listControlData.getJsonLabels());
        psb.append('}');
        return psb.toString();
    }

}
