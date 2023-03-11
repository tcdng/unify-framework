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

package com.tcdng.unify.core.convert;

import java.math.BigDecimal;

import com.tcdng.unify.convert.converters.AbstractConverter;
import com.tcdng.unify.convert.converters.ConverterFormatter;
import com.tcdng.unify.core.data.Money;
import com.tcdng.unify.core.format.DecimalFormatter;

/**
 * A value to money converter.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class MoneyConverter extends AbstractConverter<Money> {

    @Override
    protected Money doConvert(Object value, ConverterFormatter<?> formatter) throws Exception {
        if (value instanceof Money) {
            return new Money((Money) value);
        }

        if (value instanceof String) {
            String string = ((String) value).trim();
            if (!string.isEmpty()) {
                String currencyCode = string.substring(0, 3);
                String amtPart = string.substring(3).trim();
                BigDecimal amount = null;
                if (formatter == null) {
                    amount = new BigDecimal(amtPart);
                } else {
                    DecimalFormatter decimalFormatter = (DecimalFormatter) formatter;
                    amount = new BigDecimal((decimalFormatter.parse(amtPart)).toString());
                }

                return new Money(currencyCode, amount);
            }
        }

        return null;
    }

}
