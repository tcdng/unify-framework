/*
 * Copyright 2018-2019 The Code Department.
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
import com.tcdng.unify.web.ui.ListControlJsonData;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.control.SearchField;

/**
 * Search field writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(SearchField.class)
@Component("searchfield-writer")
public class SearchFieldWriter extends AbstractPopupTextFieldWriter {

    @Override
    protected void appendPopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        SearchField searchField = (SearchField) popupTextField;
        searchField.setKeyOnly(true);

        writer.write("<div");
        writeTagId(writer, searchField.getSearchPanelId());
        writeTagStyleClass(writer, "sfborder");
        writer.write(">");

        // Filter row
        writer.write("<div");
        writeTagStyleClass(writer, "sffilterrow");
        writer.write(">");
        writer.write("<span>").write(searchField.getFilterLabel()).write("</span>");
        writer.write("<input type=\"text\"");
        writeTagId(writer, searchField.getFilterId());
        writeTagValue(writer, searchField.getFilter());
        writer.write("/>");
        writer.write("</div>");

        // Result row
        writer.write("<div");
        writeTagId(writer, searchField.getResultPanelId());
        writeTagStyleClass(writer, "sfresultrow");
        writer.write(">");
        writeResultList(writer, searchField);
        writer.write("</div>");

        // Action row
        writer.write("<div");
        writeTagStyleClass(writer, "sfactionrow");
        writer.write(">");
        writeButton(writer, searchField.getClearButtonId(), "sfactbutton", "float:left;",
                getSessionMessage("button.clear"));
        writeButton(writer, searchField.getCancelButtonId(), "sfactbutton", "float:right;",
                getSessionMessage("button.cancel"));
        writer.write("</div>");

        writer.write("</div>");
    }

    @Override
    protected void appendPopupBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        SearchField searchField = (SearchField) popupTextField;
        searchField.setKeyOnly(true);
        writeRigging(writer, searchField, "ux.rigSearchField");
    }

    @Override
    protected void doWriteSectionStructureAndContent(ResponseWriter writer, Widget widget, String sectionPageName)
            throws UnifyException {
        SearchField searchField = (SearchField) widget;
        if (searchField.getResultPanelId().equals(sectionPageName)) {
            writeResultList(writer, searchField);
        }
    }

    @Override
    protected void doWriteSectionBehavior(ResponseWriter writer, Widget widget, String sectionPageName)
            throws UnifyException {
        SearchField searchField = (SearchField) widget;
        if (searchField.getResultPanelId().equals(sectionPageName)) {
            writeRigging(writer, searchField, "ux.searchWireResult");
        }
    }

    @Override
    protected String getOnShowAction() throws UnifyException {
        return "ux.searchOnShow";
    }

    @Override
    protected String getOnShowParam(AbstractPopupTextField popupTextField) throws UnifyException {
        SearchField searchField = (SearchField) popupTextField;
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"pFilId\":\"").append(searchField.getFilterId()).append('"');
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

    private void writeRigging(ResponseWriter writer, SearchField searchField, String functionName)
            throws UnifyException {
        String pageName = searchField.getId();
        ListControlJsonData listControlJsonData = searchField.getListControlJsonData(true, true, false);

        // Append rigging
        writer.write(functionName).write("({");
        writer.write("\"pId\":\"").write(pageName).write('"');
        writer.write(",\"pFacId\":\"").write(searchField.getFacadeId()).write('"');
        writer.write(",\"pFilId\":\"").write(searchField.getFilterId()).write('"');
        writer.write(",\"pRltId\":\"").write(searchField.getResultPanelId()).write('"');
        writer.write(",\"pClrId\":\"").write(searchField.getClearButtonId()).write('"');
        writer.write(",\"pCanId\":\"").write(searchField.getCancelButtonId()).write('"');
        writer.write(",\"pClearable\":").write(searchField.isClearable());
        writer.write(",\"pCmdURL\":\"").writeCommandURL().write('"');
        writer.write(",\"pICnt\":").write(listControlJsonData.getSize());
        writer.write(",\"pLabelIds\":").write(listControlJsonData.getJsonSelectIds());
        writer.write(",\"pKeys\":").write(listControlJsonData.getJsonKeys());
        writer.write("});");
    }

    private void writeResultList(ResponseWriter writer, SearchField searchField) throws UnifyException {
        List<? extends Listable> listableList = searchField.getListables();
        int length = listableList.size();
        writer.write("<div");
        writeTagStyleClass(writer, "sflist");
        writer.write(">");
        for (int i = 0; i < length; i++) {
            writer.write("<a");
            writeTagId(writer, searchField.getNamingIndexedId(i));
            if (i % 2 == 0) {
                writeTagStyleClass(writer, "odd");
            } else {
                writeTagStyleClass(writer, "even");
            }
            writer.write(">");
            writer.writeWithHtmlEscape(listableList.get(i).getListDescription());
            writer.write("</a>");
        }
        writer.write("</div>");
    }

}
