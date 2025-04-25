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
package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.format.NumberFormatter;
import com.tcdng.unify.core.format.NumberSymbols;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ui.util.WebRegexUtils;
import com.tcdng.unify.web.ui.widget.control.AbstractNumberField;
import com.tcdng.unify.web.ui.widget.control.TextField;

/**
 * Number field writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Writes(AbstractNumberField.class)
@Component("numberfield-writer")
public class NumberFieldWriter extends TextFieldWriter {

    @Override
    protected String getFormatRegex(TextField textField) throws UnifyException {
        AbstractNumberField numberField = (AbstractNumberField) textField;
        return WebRegexUtils.getNumberFormatRegex(((NumberFormatter<?>) numberField.getFormatter()).getNumberSymbols(),
        		numberField.getPrecision(), numberField.getScale(),
        		numberField.isAcceptNegative(),
                numberField.isUseGrouping(),
                numberField.isStrictFormat());
    }

    protected void addClientFormatParams(TextField textField, JsonWriter jw) throws UnifyException {
        AbstractNumberField numberField = (AbstractNumberField) textField;
    	NumberSymbols ns = ((NumberFormatter<?>) numberField.getFormatter()).getNumberSymbols();
        jw.write("grouping", numberField.isUseGrouping());
        jw.write("groupSize", ns.getGroupSize()); // TODO For Indian this should be 2
        jw.write("comma", ns.getGroupingSeparator());
        jw.write("decimal", ns.getDecimalSeparator());
        jw.write("negPrefix", ns.getNegativePrefix());
        jw.write("negSuffix", ns.getNegativeSuffix());
    }
}
