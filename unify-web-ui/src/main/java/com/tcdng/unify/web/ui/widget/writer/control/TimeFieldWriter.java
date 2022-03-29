/*
 * Copyright 2018-2022 The Code Department.
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

import java.util.Calendar;
import java.util.Date;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.format.Pattern;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.widget.control.TimeField;

/**
 * Time field writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(TimeField.class)
@Component("timefield-writer")
public class TimeFieldWriter extends AbstractPopupTextFieldWriter {

    @Override
    protected void writePopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        TimeField timeField = (TimeField) popupTextField;
        StringBuilder hsb = new StringBuilder();
        StringBuilder csb = new StringBuilder();
        StringBuilder fsb = new StringBuilder();
        Pattern[] pattern = timeField.getPattern();
        final String facId = timeField.getFacadeId();
        for (int i = 0; i < pattern.length; i++) {
            Pattern fp = pattern[i];
            if (fp.isFiller()) {
                hsb.append("<div style=\"display:table-cell\">").append("</div>");
                csb.append("<div style=\"display:table-cell\">").append(fp.getPattern()).append("</div>");
                fsb.append("<div style=\"display:table-cell\">").append("</div>");
            } else {
                char plusBtnSymbol = '+';
                char minusBtnSymbol = '-';
                hsb.append("<div style=\"display:table-cell\"><button type=\"button\" class=\"tfbutton\" id=\"")
                        .append(timeField.getPrefixedId("btnpos_")).append(i).append("\">").append(plusBtnSymbol)
                        .append("</button></div>");
                csb.append(
                        "<div style=\"display:table-cell\"><input type=\"text\" class=\"tftext\" id=\"")
                        .append(facId).append(i).append("\"").append(" readonly />");
                csb.append("</div>");
                fsb.append("<div style=\"display:table-cell\"><button type=\"button\" class=\"tfbutton\" id=\"")
                        .append(timeField.getPrefixedId("btnneg_")).append(i).append("\">").append(minusBtnSymbol)
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
    protected void doWritePopupTextFieldBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField,
            boolean popupEnabled) throws UnifyException {
        TimeField timeField = (TimeField) popupTextField;
        writer.beginFunction("ux.rigTimeField");
        writer.writeParam("pId", timeField.getId());
        writer.writeParam("pFacId", timeField.getFacadeId());
        writer.writeParam("pClearable", timeField.isClearable());
        writer.writeParam("pPattern", timeField.getPattern());
        writer.writeParam("pLists", timeField.getDateTimeFormat());
        Date date = timeField.getValue(Date.class);
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            writer.writeParam("pHour", cal.get(Calendar.HOUR_OF_DAY));
            writer.writeParam("pMinute", cal.get(Calendar.MINUTE));
            writer.writeParam("pSecond", cal.get(Calendar.SECOND));
        }
        writer.writeParam("pEnabled", popupEnabled);
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
        return null;
    }

    @Override
    protected String getOnHideParam(AbstractPopupTextField popupTextField) throws UnifyException {
        return null;
    }

}
