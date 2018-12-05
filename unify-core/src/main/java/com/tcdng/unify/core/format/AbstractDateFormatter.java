/*
 * Copyright 2014 The Code Department
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.DateStyleConstants;
import com.tcdng.unify.core.data.SimpleDateFormatPool;
import com.tcdng.unify.core.util.CalendarUtils;

/**
 * Abstract base class for a date formatter.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "style", type = String.class),
		@UplAttribute(name = "timeZoneId", type = String.class), @UplAttribute(name = "pattern", type = String.class) })
public abstract class AbstractDateFormatter extends AbstractFormatter<Date> implements DateFormatter {

	protected enum TYPE {
		DATE, TIME, DATETIME, FIXED
	};

	private TYPE type;

	private String pattern;

	private SimpleDateFormatPool sdfp;

	private static final Map<String, Integer> dateStyleMap = new HashMap<String, Integer>();

	static {
		dateStyleMap.put(DateStyleConstants.DEFAULT_STYLE, DateFormat.DEFAULT);
		dateStyleMap.put(DateStyleConstants.SHORT_STYLE, DateFormat.SHORT);
		dateStyleMap.put(DateStyleConstants.MEDIUM_STYLE, DateFormat.MEDIUM);
		dateStyleMap.put(DateStyleConstants.LONG_STYLE, DateFormat.LONG);
		dateStyleMap.put(DateStyleConstants.FULL_STYLE, DateFormat.FULL);
	}

	protected AbstractDateFormatter(TYPE type) {
		super(Date.class);
		this.type = type;
	}

	@Override
	public String getPattern() throws UnifyException {
		this.getSimpleDateFormatPool();
		return this.pattern;
	}

	@Override
	public String format(Date date) throws UnifyException {
		return this.getSimpleDateFormatPool().format(date);
	}

	@Override
	public Date parse(String string) throws UnifyException {
		return this.getSimpleDateFormatPool().parse(string);
	}

	private SimpleDateFormatPool getSimpleDateFormatPool() throws UnifyException {
		if (this.sdfp == null) {
			this.sdfp = CalendarUtils.getSimpleDateFormatPool(this.getSessionContext().getLocale(), getDatePattern());
		}
		return this.sdfp;
	}

	private String getDatePattern() throws UnifyException {
		DateFormat df = null;

		switch (this.type) {
		case DATETIME:
			Integer styleId = this.getStyleId();
			df = DateFormat.getDateTimeInstance(styleId, styleId, this.getLocale());
			break;
		case TIME:
			df = DateFormat.getTimeInstance(this.getStyleId(), this.getLocale());
			break;
		case DATE:
			df = DateFormat.getDateInstance(this.getStyleId(), this.getLocale());
			break;
		case FIXED:
		default:
			String uplPattern = this.getUplAttribute(String.class, "pattern");
			df = new SimpleDateFormat(uplPattern, this.getLocale());
			break;
		}

		String timeZoneId = this.getUplAttribute(String.class, "timeZoneId");
		if (timeZoneId != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		}

		SimpleDateFormat sdf = (SimpleDateFormat) df;
		if (DateStyleConstants.CUSTOMSHORT_STYLE.equals(this.getUplAttribute(String.class, "style"))) {
			sdf.applyPattern(this.getFormatHelper().getDatePatternWithLongYear(sdf.toPattern()));
		}

		this.pattern = sdf.toPattern();
		return this.pattern;
	}

	private Integer getStyleId() throws UnifyException {
		Integer id = null;
		String style = this.getUplAttribute(String.class, "style");
		if (style != null) {
			String actStyle = style;
			if (DateStyleConstants.CUSTOMSHORT_STYLE.equals(actStyle)) {
				actStyle = DateStyleConstants.SHORT_STYLE;
			}
			id = dateStyleMap.get(actStyle);
		}
		if (id == null) {
			id = dateStyleMap.get(DateStyleConstants.DEFAULT_STYLE);
		}
		return id;
	}
}
