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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.ListControlJsonData;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.control.SingleSelect;

/**
 * Single select field writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(SingleSelect.class)
@Component("singleselect-writer")
public class SingleSelectWriter extends AbstractPopupTextFieldWriter {

    @Override
    protected void appendPopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        SingleSelect singleSelect = (SingleSelect) popupTextField;

        writer.write("<div id=\"").write(singleSelect.getFramePanelId())
                .write("\" class=\"ssborder\" style=\"overflow-y:auto;overflow-x:hidden;\" tabindex=\"0\">");
        writer.write("<div id=\"").write(singleSelect.getListPanelId()).write("\" class=\"sslist\">");
        String value = singleSelect.getStringValue();
        List<? extends Listable> listableList = singleSelect.getListables();
        int length = listableList.size();

        String blankOption = singleSelect.getBlankOption();
        String selStyleClass = getUserColorStyleClass("sel");
        if (blankOption != null) {
            writer.write("<a id=\"").write(singleSelect.getBlankOptionId()).write("\" class=\"");
            if (StringUtils.isBlank(value)) {
                writer.write(selStyleClass).write("\">");
            } else {
                writer.write("norm\">");
            }

            if (StringUtils.isBlank(blankOption)) {
                writer.writeHtmlFixedSpace();
            } else {
                writer.writeWithHtmlEscape(blankOption);

            }

            writer.write("</a>");
        }

        Formatter<Object> formatter = singleSelect.getFormatter();
        for (int i = 0; i < length; i++) {
            Listable listable = listableList.get(i);
            String key = listable.getListKey();
            writer.write("<a");
            writeTagId(writer, singleSelect.getNamingIndexedId(i));
            if (key.equals(value)) {
                writeTagStyleClass(writer, selStyleClass);
            } else {
                writeTagStyleClass(writer, "norm");
            }
            writer.write(">");
            if (formatter != null) {
                writer.writeWithHtmlEscape(formatter.format(listable.getListDescription()));
            } else {
                writer.writeWithHtmlEscape(listable.getListDescription());
            }
            writer.write("</a>");
        }
        writer.write("</div>");
        writer.write("</div>");
    }

    @Override
    protected void appendPopupBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        SingleSelect singleSelect = (SingleSelect) popupTextField;
        ListControlJsonData listControlJsonData = singleSelect.getListControlJsonData(true, true, false);

        // Append rigging
        String blankOption = singleSelect.getBlankOption();
        writer.write("ux.rigSingleSelect({");
        writer.write("\"pId\":\"").write(singleSelect.getId()).write('"');
        writer.write(",\"pFacId\":\"").write(singleSelect.getFacadeId()).write('"');
        writer.write(",\"pFrmId\":\"").write(singleSelect.getFramePanelId()).write('"');
        writer.write(",\"pLstId\":\"").write(singleSelect.getListPanelId()).write('"');
        writer.write(",\"pBlnkId\":\"").write(singleSelect.getBlankOptionId()).write('"');
        writer.write(",\"pKeyIdx\":").write(listControlJsonData.getValueIndex());
        writer.write(",\"pICnt\":").write(listControlJsonData.getSize());
        writer.write(",\"pLabelIds\":").write(listControlJsonData.getJsonSelectIds());
        writer.write(",\"pKeys\":").write(listControlJsonData.getJsonKeys());
        writer.write(",\"pIsBlank\":").write(blankOption != null);
        writer.write(",\"pNormCls\":\"norm\"");
        writer.write(",\"pSelCls\":\"").write(getUserColorStyleClass("sel")).write("\"");
        writer.write("});");
    }

    @Override
    protected String getOnShowAction() throws UnifyException {
        return "ux.singleSelectOnShow";
    }

    @Override
    protected String getOnShowParam(AbstractPopupTextField popupTextField) throws UnifyException {
        SingleSelect singleSelect = (SingleSelect) popupTextField;
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"pFrmId\":\"").append(singleSelect.getFramePanelId()).append('"');
        sb.append('}');
        return sb.toString();
    }

    @Override
    protected String getOnHideAction() throws UnifyException {
        return null;
    }

    @Override
    protected String getOnHideParam(AbstractPopupTextField popupTextField) throws UnifyException {
        return null;
    }

}
