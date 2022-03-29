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
package com.tcdng.unify.web.ui.widget.control;

import java.math.BigDecimal;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.widget.AbstractFormattedControl;

/**
 * A debit-credit label control.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-debitcreditlabel")
@UplAttributes({ @UplAttribute(name = "negativeCredit", type = boolean.class, defaultVal = "true") })
public class DebitCreditLabel extends AbstractFormattedControl {

    public DebitCreditLabel() {
        super.setEditable(false);
    }

    @Override
    public void setEditable(boolean editable) {

    }

    @Override
    public String getStringValue() throws UnifyException {
        BigDecimal val = getValue(BigDecimal.class);
        if (val != null) {
            String formatted = DataUtils.convert(String.class, val.abs(), getFormatter());
            boolean debit = val.compareTo(BigDecimal.ZERO) < 0;
            debit = isNegativeCredit()? !debit : debit;
            return formatted + (debit ? " Dr": " Cr");
        }

        return null;
    }

    @Override
    public boolean isSupportReadOnly() {
        return false;
    }

    @Override
    public boolean isSupportDisabled() {
        return false;
    }

    public boolean isNegativeCredit() throws UnifyException {
        return getUplAttribute(boolean.class, "negativeCredit");
    }

}
