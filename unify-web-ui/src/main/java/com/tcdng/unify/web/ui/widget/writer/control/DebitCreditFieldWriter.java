/*
 * Copyright 2018-2023 The Code Department.
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
import com.tcdng.unify.core.constant.DrCrType;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.DebitCreditField;
import com.tcdng.unify.web.ui.widget.control.TextField;

/**
 * Debit/Credit field writer.
 * 
 * @author The Code Department
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
        if (!DrCrType.OPTIONAL.equals(debitCreditField.getType()) || debitCreditField.isContainerDisabled()
                || !debitCreditField.isContainerEditable()) {
            writer.write(" disabled");
            writeTagStyleClass(writer, "tpbutton npoint");
        } else {
            writeTagStyleClass(writer, "tpbutton");
            writeTagTitle(writer, "Toggle (Dr <-> Cr"); // TODO Get from messages
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
    protected String getFacadeHiddenStringValue(TextField textField) throws UnifyException {
        BigDecimal val = textField.getValue(BigDecimal.class);
        if (val != null) {
            return DataUtils.convert(String.class, val, null);
        }

        return null;
    }

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);
		DebitCreditField debitCreditField = (DebitCreditField) widget;
		final String[] options = { "Dr", "Cr" }; // TODO Get from messages
		final String[] prefixes = debitCreditField.isNegativeCredit() ? new String[] { "", "-" }
				: new String[] { "-", "" };
		DrCrType type = debitCreditField.getType();
		if (DrCrType.OPTIONAL.equals(type)) {
			type = DrCrType.DEBIT;
			BigDecimal val = debitCreditField.getValue(BigDecimal.class);
			if (val != null) {
				boolean neg = val.compareTo(BigDecimal.ZERO) < 0;
				if ((debitCreditField.isNegativeCredit() && neg) || (!debitCreditField.isNegativeCredit() && !neg)) {
					type = DrCrType.CREDIT;
				}
			}
		}

		writer.beginFunction("ux.rigDebitCreditField");
		writer.writeParam("pId", debitCreditField.getId());
		writer.writeParam("pFacId", debitCreditField.getFacadeId());
		writer.writeParam("pBtnId", debitCreditField.getButtonId());
		writer.writeParam("pIndex", type.index());
		writer.writeParam("pPrefixes", prefixes);
		writer.writeParam("pOptions", options);
		writer.endFunction();
	}

}
