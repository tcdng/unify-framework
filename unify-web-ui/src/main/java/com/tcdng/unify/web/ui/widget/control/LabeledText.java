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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.web.ui.widget.AbstractFormattedControl;

/**
 * A labeled text control.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-labeledtext")
@UplAttributes({
	@UplAttribute(name = "maxLen", type = int.class),
	@UplAttribute(name = "useDateFormatOverride", type = boolean.class, defaultVal = "false") })
public class LabeledText extends AbstractFormattedControl {

	public boolean isUseDateFormatOverride() throws UnifyException {
		return getUplAttribute(boolean.class, "useDateFormatOverride");
	}

	@Override
	public Formatter<Object> getFormatter() throws UnifyException {
		Formatter<Object> formatter = super.getFormatter();
		if (formatter.isStrictFormat()) {
			return formatter;
		}

		Formatter<Object> overrideFormatter = isUseDateFormatOverride() ? getWidgetDateFormatOverrideFormatter() : null;
		return overrideFormatter != null ? overrideFormatter : formatter;
	}

}
