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

import java.math.BigDecimal;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.DebitCreditField;
import com.tcdng.unify.web.ui.widget.control.TextField;

/**
 * Debit/Credit field writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(DebitCreditField.class)
@Component("debitcreditfield-writer")
public class DebitCreditFieldWriter extends NumberFieldWriter {

    @Override
    protected void writeTrailingAddOn(ResponseWriter writer, Widget widget) throws UnifyException {
        DebitCreditField debitCreditField = (DebitCreditField) widget;
        writer.write("<button");
        writeTagId(writer, debitCreditField.getButtonId());
        writeTagStyleClass(writer, "tpbutton");
        if (debitCreditField.isContainerDisabled() || !debitCreditField.isContainerEditable()) {
            writer.write(" disabled");
        }

        writer.write(">");
        writer.write("</button>");
    }

    @Override
    protected String getFacadeStringValue(TextField textField) throws UnifyException {
        BigDecimal val = textField.getValue(BigDecimal.class);
        if (val != null) {
            return DataUtils.convert(String.class, val.abs(), textField.getFormatter());
        }

        return null;
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);
        DebitCreditField debitCreditField = (DebitCreditField) widget;
        final String[] options = {"Dr", "Cr"};
        writer.beginFunction("ux.rigDebitCreditField");
        writer.writeParam("pId", debitCreditField.getId());
        writer.writeParam("pFacId", debitCreditField.getFacadeId());
        writer.writeParam("pBtnId", debitCreditField.getButtonId());
        writer.writeParam("pOptions", options);
        writer.endFunction();
    }

}
