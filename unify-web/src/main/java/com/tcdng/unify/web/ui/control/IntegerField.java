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
package com.tcdng.unify.web.ui.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.format.NumberFormatter;

/**
 * Input field for entering whole numbers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-integer")
@UplAttributes({ @UplAttribute(name = "formatter", type = Formatter.class, defaultValue = "$d{!integerformat}") })
public class IntegerField extends AbstractNumberField {

    @Override
    public void onPageInitialize() throws UnifyException {
        NumberFormatter<?> numberFormatter = (NumberFormatter<?>) getFormatter();
        numberFormatter.setPrecision(getUplAttribute(int.class, "precision"));
        numberFormatter.setGroupingUsed(getUplAttribute(boolean.class, "useGrouping"));

        super.onPageInitialize();
    }
}
