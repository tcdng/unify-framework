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
package com.tcdng.unify.web.ui.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.format.DateTimeFormat;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.format.Pattern;

/**
 * Input control for presenting and capturing a date.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-date")
@UplAttributes({ @UplAttribute(name = "buttonImgSrc", type = String.class, defaultValue = "$t{images/calendar.png}"),
		@UplAttribute(name = "formatter", type = Formatter.class, defaultValue = "$d{!dateformat style:customshort}") })
public class DateField extends AbstractTimeField {

	private DateTimeFormat monthDateTimeFormat;

	private String[] shortDayList;

	private String[] longMonthList;

	@Override
	public void onPageInitialize() throws UnifyException {
		super.onPageInitialize();

		// Preset date-time format for month and pattern aliases
		Formatter<?> formatter = this.getFormatter();
		for (Pattern p : super.getPattern()) {
			if (!p.isFiller()) {
				if ('M' == p.getPattern().charAt(0)) {
					this.monthDateTimeFormat = formatter.getFormatHelper().getSubPatternDateTimeFormat(p.getPattern(),
							formatter.getLocale());
					this.longMonthList = monthDateTimeFormat.getSubPatternDescriptions();
				}
			}
		}

		// Preset short days list
		this.shortDayList = formatter.getFormatHelper().getSubPatternDateTimeFormat("EEE", formatter.getLocale())
				.getSubPatternKeys();
	}

	@Override
	public Pattern[] getPattern() throws UnifyException {
		Pattern[] pattern = super.getPattern();
		for (Pattern p : pattern) {
			if (!p.isFiller()) {
				switch (p.getPattern().charAt(0)) {
				case 'y':
					p.setTarget(this.getPrefixedId("year_"));
					break;
				case 'd':
					p.setTarget(this.getPrefixedId("day_"));
					break;
				case 'M':
					p.setTarget(this.getPrefixedId("mon_"));
					break;
				default:
				}
			}
		}
		return pattern;
	}

	public String[] getShortDayList() {
		return this.shortDayList;
	}

	public String[] getLongMonthList() {
		return this.longMonthList;
	}
}
