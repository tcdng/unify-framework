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
package com.tcdng.unify.web.ui.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.format.NumberFormatter;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.control.AbstractNumberField;
import com.tcdng.unify.web.ui.control.TextField;

/**
 * Number field writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(AbstractNumberField.class)
@Component("numberfield-writer")
public class NumberFieldWriter extends TextFieldWriter {

	@Override
	protected void writeFormatRegex(ResponseWriter writer, TextField textField) throws UnifyException {
		AbstractNumberField numberField = (AbstractNumberField) textField;
		int scale = 0;
		if (textField.isUplAttribute("scale")) {
			scale = numberField.getUplAttribute(int.class, "scale");
		}

		writer.writeNumberFormatRegex(((NumberFormatter<?>) numberField.getFormatter()).getNumberSymbols(),
				numberField.getUplAttribute(int.class, "precision"), scale,
				numberField.getUplAttribute(boolean.class, "acceptNegative"),
				numberField.getUplAttribute(boolean.class, "useGrouping"));
	}
}
