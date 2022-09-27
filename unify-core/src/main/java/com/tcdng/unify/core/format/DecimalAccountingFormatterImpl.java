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
package com.tcdng.unify.core.format;

import java.text.ParseException;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Default decimal (accounting) number formatter implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = "decimalaccountingformat", description = "$m{format.decimal.accounting}")
public class DecimalAccountingFormatterImpl extends AbstractNumberFormatter<Number> implements DecimalFormatter {

	public DecimalAccountingFormatterImpl() {
		super(Number.class, NumberType.DECIMAL_ACCOUNTING);
	}

	@Override
	public Number parse(String string) throws UnifyException {
		try {
			string = ensureParsable(string);
			return getNumberFormat().parse(string);
		} catch (ParseException e) {
			throwOperationErrorException(e);
		}
		return null;
	}

	protected DecimalAccountingFormatterImpl(NumberType type) {
		super(Number.class, type);
	}
}
