/*
 * Copyright 2018 The Code Department
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

import java.util.Date;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.format.DateTimeFormat;
import com.tcdng.unify.core.format.Pattern;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.control.TimeField;

/**
 * Time field writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(TimeField.class)
@Component("timefield-writer")
public class TimeFieldWriter extends AbstractPopupTextFieldWriter {

    @Override
    protected void appendPopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        TimeField timeField = (TimeField) popupTextField;
        Date date = timeField.getValue(Date.class);
        if (date == null) {
            date = new Date();
        }

        StringBuilder hsb = new StringBuilder();
        StringBuilder csb = new StringBuilder();
        StringBuilder fsb = new StringBuilder();
        Pattern[] pattern = timeField.getPattern();
        DateTimeFormat[] dateTimeFormat = timeField.getDateTimeFormat();
        for (int i = 0; i < pattern.length; i++) {
            Pattern fp = pattern[i];
            if (fp.isFiller()) {
                hsb.append("<div style=\"display:table-cell\">").append("</div>");
                csb.append("<div style=\"display:table-cell\">").append(fp.getPattern()).append("</div>");
                fsb.append("<div style=\"display:table-cell\">").append("</div>");
            } else {
                char plusBtnSymbol = '+';
                char minusBtnSymbol = '-';
                List<? extends Listable> listableList = dateTimeFormat[i].getList();
                String value = dateTimeFormat[i].format(date);
                hsb.append("<div style=\"display:table-cell\"><button type=\"button\" class=\"tfbutton\" id=\"")
                        .append(timeField.getPrefixedId("btnat_")).append(i).append("\">").append(plusBtnSymbol)
                        .append("</button></div>");
                csb.append(
                        "<div style=\"display:table-cell\"><input type=\"text\" class=\"tftext\" readonly=\"true\" id=\"")
                        .append(fp.getTarget()).append("\" value=\"").append(value).append("\"/>");
                if (listableList != null) {
                    int j = 0;
                    for (Listable listable : listableList) {
                        if (listable.getListKey().equals(value)) {
                            break;
                        }
                        j++;
                    }
                    csb.append("<input type=\"hidden\" id=\"").append("h_").append(fp.getTarget()).append("\" value=\"")
                            .append(j).append("\"/>");
                }
                csb.append("</div>");
                fsb.append("<div style=\"display:table-cell\"><button type=\"button\" class=\"tfbutton\" id=\"")
                        .append(timeField.getPrefixedId("btnst_")).append(i).append("\">").append(minusBtnSymbol)
                        .append("</button></div>");
            }
        }

        hsb.append("<div style=\"display:table-cell\"><button type=\"button\" class=\"tfabutton\" id=\"")
                .append(timeField.getPrefixedId("btns_")).append("\">").append(getSessionMessage("button.set"))
                .append("</button></div>");
        csb.append("<div style=\"display:table-cell\"><button type=\"button\" class=\"tfabutton\" id=\"")
                .append(timeField.getPrefixedId("btncl_")).append("\">").append(getSessionMessage("button.clear"))
                .append("</button></div>");
        fsb.append("<div style=\"display:table-cell\"><button type=\"button\" class=\"tfabutton\" id=\"")
                .append(timeField.getPrefixedId("btncn_")).append("\">").append(getSessionMessage("button.cancel"))
                .append("</button></div>");

        writer.write("<div class=\"tfframe\"><div style=\"display:table\"><div style=\"display:table-row\">")
                .write(hsb.toString()).write("</div><div style=\"display:table-row\">").write(csb.toString())
                .write("</div><div style=\"display:table-row\">").write(fsb.toString()).write("</div></div></div>");
    }

    @Override
    protected void appendPopupBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        TimeField timeField = (TimeField) popupTextField;
        String pageName = timeField.getId();
        writer.write("ux.rigTimeField({");
        writer.write("\"pId\":\"").write(pageName).write('"');
        writer.write(",\"pClearable\":").write(timeField.isClearable());
        writer.write(",\"pPattern\":");
        writer.writeJsonPatternObject(timeField.getPattern());
        writer.write(",\"pFormat\":");
        writer.writeJsonDateTimeFormatObject(timeField.getDateTimeFormat());
        writer.write("});");
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
        return null;
    }

    @Override
    protected String getOnHideParam(AbstractPopupTextField popupTextField) throws UnifyException {
        return null;
    }

}
