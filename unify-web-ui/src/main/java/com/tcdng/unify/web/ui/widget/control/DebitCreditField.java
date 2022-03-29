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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.DrCrType;
import com.tcdng.unify.web.constant.ExtensionType;

/**
 * Debit/Credit field.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-debitcredit")
@UplAttributes({
    @UplAttribute(name = "acceptNegative", type = boolean.class, defaultVal = "false"),
    @UplAttribute(name = "negativeCredit", type = boolean.class, defaultVal = "true"),
    @UplAttribute(name = "type", type = DrCrType.class, defaultVal = "optional")})
public class DebitCreditField extends DecimalField {

    @Override
    public ExtensionType getExtensionType() {
        return ExtensionType.FACADE_HIDDEN_EDIT;
    }

    @Override
    public boolean isUseFacade() throws UnifyException {
        return true;
    }

    @Override
    public boolean isUseFacadeFocus() throws UnifyException {
        return true;
    }

    public String getButtonId() throws UnifyException {
        return getPrefixedId("btn_");
    }

    public boolean isNegativeCredit() throws UnifyException {
        return getUplAttribute(boolean.class, "negativeCredit");
    }

    public DrCrType getType() throws UnifyException {
        return getUplAttribute(DrCrType.class, "type");
    }
}
