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

import java.util.Calendar;
import java.util.Date;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.control.AbstractPopupTextField;
import com.tcdng.unify.web.ui.widget.control.DateField;
import com.tcdng.unify.web.ui.widget.control.DateFieldType;

/**
 * Date field writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(DateField.class)
@Component("datefield-writer")
public class DateFieldWriter extends AbstractPopupTextFieldWriter {

    @Override
    protected void writePopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException {
        DateField dateField = (DateField) popupTextField;
        final DateFieldType type = dateField.getType();
        writer.write("<div");
        writeTagId(writer, dateField.getPrefixedId("cal_"));
        writeTagStyleClass(writer, "dborder");
        writer.write(">");

        // Top section
        writer.write("<div class=\"cnav\">");
        if (type.supportsYear()) {
            writeButton(writer, dateField.getPrefixedId("decy_"), "cscroll", null, "<<");
        }
        
        writeButton(writer, dateField.getPrefixedId("decm_"), "cscroll", null, "<");
        writer.write("<div class=\"cdisplay\">");
        writer.write("<span");
        writeTagId(writer, dateField.getPrefixedId("disp1_"));
        writer.write("></span>");
        writer.write("<span");
        writeTagId(writer, dateField.getPrefixedId("disp2_"));
        writer.write("></span>");
        writer.write("</div>");
        writeButton(writer, dateField.getPrefixedId("incm_"), "cscroll", null, ">");
        if (type.supportsYear()) {
            writeButton(writer, dateField.getPrefixedId("incy_"), "cscroll", null, ">>");
        }
        
        writer.write("</div>");

        // Mid section
        writer.write("<div style=\"position:relative;\">");
        writer.write("<div");
        writeTagId(writer, dateField.getPrefixedId("cont_"));
        writeTagStyleClass(writer, "ccontent");
        writer.write(">");
        writer.write("</div>");

        writer.write("<div");
        writeTagId(writer, dateField.getPrefixedId("month_"));
        writeTagStyleClass(writer, "clist");
        writer.write(">");
        writer.write("</div>");

        writer.write("<div");
        writeTagId(writer, dateField.getPrefixedId("year_"));
        writeTagStyleClass(writer, "clist");
        writer.write(">");
        writer.write("</div>");
        
        writer.write("</div>");

        // Bottom section
        writer.write("<div class=\"ccontrol\">");
        if (type.supportsYear()) {
            writeButton(writer, dateField.getPrefixedId("btnt_"), "cactbutton", "float:left;",
                    getSessionMessage("button.today"));
        }
        
        writeButton(writer, dateField.getPrefixedId("btnc_"), "cactbutton", "float:right;",
                getSessionMessage("button.clear"));
        writer.write("</div>");
        writer.write("</div>");
    }

    @Override
    protected void doWritePopupTextFieldBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField,
            boolean popupEnabled) throws UnifyException {
        DateField dateField = (DateField) popupTextField;
        writer.beginFunction("ux.rigDateField");
        writer.writeParam("pId", dateField.getId());
        writer.writeParam("pDayClass", "cday");
        writer.writeParam("pDisableClass", "cdis");
        writer.writeParam("pCurrClass", "ccurrent");
        writer.writeParam("pTodayClass", "ctoday");
        writer.writeParam("pClearable", dateField.isClearable());
        writer.writeParam("pShortDayNm", dateField.getShortDayList());
        writer.writeParam("pLongMonthNm", dateField.getLongMonthList());
        writer.writeParam("pPattern", dateField.getPattern());
        writer.writeParam("pType", dateField.getType().toString().toLowerCase());
        Date date = dateField.getValue(Date.class);
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            writer.writeParam("pDay", cal.get(Calendar.DAY_OF_MONTH));
            writer.writeParam("pMonth", cal.get(Calendar.MONTH));
            writer.writeParam("pYear", cal.get(Calendar.YEAR));
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
