/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.convert;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.common.constants.StandardFormatType;

/**
 * Format context.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class FormatContext {

	private Map<StandardFormatType, Format> formats;

	public FormatContext() {
		this.formats = new HashMap<StandardFormatType, Format>();
	}

	public Format getFormatByCode(String typeCode) {
		StandardFormatType type = StandardFormatType.fromCode(typeCode);
		if (type != null) {
			return getFormat(type);
		}

		return null;
	}

	public Format getFormat(StandardFormatType type) {
		if (type != null) {
			Format format = formats.get(type);
			if (format == null) {
				if (type.isDateType()) {
					format = new SimpleDateFormat(type.format());
				} else {
					format = new DecimalFormat(type.format());
				}
				formats.put(type, format);
			}

			return format;
		}

		return null;
	}
}
