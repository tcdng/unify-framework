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
package com.tcdng.unify.core.data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formats.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class Formats {

	public static final Formats DEFAULT = new Formats();

	private String integerFormat;

	private String decimalFormat;

	private String dateFormat;

	private String timestampFormat;

	private Formats() {
		this(null, null, null, null);
	}

	public Formats(String integerFormat, String decimalFormat, String dateFormat, String timestampFormat) {
		this.integerFormat = integerFormat != null ? integerFormat : "###,###";
		this.decimalFormat = decimalFormat != null ? decimalFormat : "###,###.00";
		this.dateFormat = dateFormat != null ? dateFormat : "yyyy-MM-dd";
		this.timestampFormat = timestampFormat != null ? timestampFormat : "yyyy-MM-dd HH:mm:ss";
	}

	public String getIntegerFormat() {
		return integerFormat;
	}

	public String getDecimalFormat() {
		return decimalFormat;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public String getTimestampFormat() {
		return timestampFormat;
	}

	public Case createCase() {
		return new Case();
	}
	
	public class Case {

		private DecimalFormat idf;

		private DecimalFormat df;

		private SimpleDateFormat sdf;

		private SimpleDateFormat tsdf;

		private Case() {
			this.idf = new DecimalFormat(integerFormat);
			this.df = new DecimalFormat(decimalFormat);
			this.sdf = new SimpleDateFormat(dateFormat);
			this.tsdf = new SimpleDateFormat(timestampFormat);
		}

		public String format(Object val) {
			if (val != null) {
				return String.valueOf(val);
			}

			return null;
		}

		public String format(Number val) {
			if (val != null) {
				if (val instanceof Integer || val instanceof Long || val instanceof Short) {
					return idf.format(val);
				}

				return df.format(val);
			}

			return null;
		}

		public String format(Date val) {
			if (val != null) {
				return sdf.format(val);
			}

			return null;
		}

		public String formatAsDate(Date val) {
			if (val != null) {
				return sdf.format(val);
			}

			return null;
		}

		public String formatAsTimestamp(Date val) {
			if (val != null) {
				return tsdf.format(val);
			}

			return null;
		}
	}
}