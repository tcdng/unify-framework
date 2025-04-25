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
package com.tcdng.unify.web.ui.widget;

import java.util.Collection;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.format.NumberFormatter;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.DataTransferBlock;

/**
 * Serves as a convenient base class for controls with formatted data.
 * 
 * @author The Code Department
 * @since 4.1
 */
@UplAttributes({
		@UplAttribute(name = "resolve", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "formatter", type = Formatter.class),
		@UplAttribute(name = "formatOverride", type = String.class) })
public abstract class AbstractFormattedControl extends AbstractControl {

	@Override
	public void populate(DataTransferBlock transferBlock) throws UnifyException {
		String binding = getBinding();
		if (binding != null) {
			getValueStore().store(transferBlock.getItemIndex(), binding, transferBlock.getValue(), getFormatter());
		}
	}

	@Override
	public <T> T getValue(Class<T> clazz) throws UnifyException {
		return DataUtils.convert(clazz, getValue(), getFormatter());
	}

	@Override
	public String getStringValue() throws UnifyException {
		return isResolve() ? resolveSessionMessage(DataUtils.convert(String.class, getValue(), getFormatter()))
				: DataUtils.convert(String.class, getValue(), getFormatter());
	}

	@Override
	public <T, U extends Collection<T>> U getValue(Class<U> clazz, Class<T> dataClass) throws UnifyException {
		return DataUtils.convert(clazz, dataClass, getValue(), getFormatter());
	}

	public final String getFormatOverride() throws UnifyException {
		return getUplAttribute(String.class, "formatOverride");
	}

	public final boolean isResolve() throws UnifyException {
		return getUplAttribute(boolean.class, "resolve");
	}

	@SuppressWarnings("unchecked")
	public Formatter<Object> getFormatter() throws UnifyException {
		Formatter<Object> formatter = (Formatter<Object>) getUplAttribute(Formatter.class, "formatter");
		if (formatter != null && formatter.isStrictFormat()) {
			return formatter;
		}

		final String formatOverride = getFormatOverride();
		Formatter<Object> overrideFormatter = !StringUtils.isBlank(formatOverride) ? getSessionFormatter(formatOverride)
				: null;
		return overrideFormatter != null ? overrideFormatter : formatter;
	}

	@Override
	public void setPrecision(int precision) throws UnifyException {
		Formatter<?> formatter = getFormatter();
		if (formatter instanceof NumberFormatter) {
			((NumberFormatter<?>) formatter).setPrecision(precision);
		}
	}

	@Override
	public void setScale(int scale) throws UnifyException {
		Formatter<?> formatter = getFormatter();
		if (formatter instanceof NumberFormatter) {
			((NumberFormatter<?>) formatter).setScale(scale);
		}
	}
}
