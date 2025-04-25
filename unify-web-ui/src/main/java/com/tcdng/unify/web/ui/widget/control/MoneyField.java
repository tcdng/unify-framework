/*
 * Copyright 2018-2025 The Code Department.
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
import com.tcdng.unify.core.data.Money;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.format.NumberFormatter;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.constant.ExtensionType;
import com.tcdng.unify.web.ui.widget.ListParamType;
import com.tcdng.unify.web.ui.widget.WriteWork;

/**
 * Represents an input field for capturing money.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-money")
@UplAttributes({ @UplAttribute(name = "currency", type = String.class),
        @UplAttribute(name = "precision", type = int.class),
        @UplAttribute(name = "scale", type = int.class),
        @UplAttribute(name = "acceptNegative", type = boolean.class),
        @UplAttribute(name = "useGrouping", type = boolean.class),
        @UplAttribute(name = "list", type = String.class, defaultVal = "currencylist"),
        @UplAttribute(name = "listParamType", type = ListParamType.class, defaultVal = "immediate"),
        @UplAttribute(name = "formatter", type = Formatter.class, defaultVal = "$d{!decimalformat}"),
        @UplAttribute(name = "extStyleClass", type = String.class, defaultVal = "trread"),
        @UplAttribute(name = "extReadOnly", type = boolean.class, defaultVal = "false") })
public class MoneyField extends AbstractListPopupTextField {

    @Override
    public void onPageConstruct() throws UnifyException {
        NumberFormatter<?> numberFormatter = (NumberFormatter<?>) getFormatter();
        int scale = getUplAttribute(int.class, "scale");
        numberFormatter.setScale(scale);

        super.onPageConstruct();
    }

    @Override
    public boolean isMultiple() {
        return true;
    }

    @Override
    public ExtensionType getExtensionType() {
        return ExtensionType.FACADE_HIDDEN;
    }

    @Override
    public boolean isPopupOnEditableOnly() {
        return true;
    }

    @Override
    public boolean isBindEventsToFacade() throws UnifyException {
        return false;
    }

    @Override
    public boolean isOpenPopupOnFac() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public String getAmountString() throws UnifyException {
        Money money = getMoney();
        if (money != null) {
            StringBuilder sb = new StringBuilder();
            Formatter<BigDecimal> formatter = getUplAttribute(Formatter.class, "formatter");
            if (money.getAmount() != null) {
                sb.append(formatter.format(money.getAmount()));
            }

            return sb.toString();
        }

        return DataUtils.EMPTY_STRING;
    }

    public String getCurrencyString() throws UnifyException {
        Money money = getMoney();
        return (money != null && money.getCurrencyCode() != null) ? money.getCurrencyCode()
                : getUplAttribute(String.class, "currency");
    }

    public String getCurrencyCode() throws UnifyException {
        return getUplAttribute(String.class, "currency");
    }

    public boolean isMultiCurrency() throws UnifyException {
        return getUplAttribute(String.class, "currency") == null;
    }
    
    public String getFramePanelId() throws UnifyException {
        return getPrefixedId("frm_");
    }

    public String getListPanelId() throws UnifyException {
        return getPrefixedId("lst_");
    }

    private Money getMoney() throws UnifyException {
        WriteWork writeWork = getWriteWork();
        Money money = writeWork.get(Money.class, "money");
        if (money == null) {
            money = getValue(Money.class);
            writeWork.set("money", money);
        }
        
        return money;
    }
}
