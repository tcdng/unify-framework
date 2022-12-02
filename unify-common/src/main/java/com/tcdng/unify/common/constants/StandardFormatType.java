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
package com.tcdng.unify.common.constants;

import com.tcdng.unify.common.util.EnumUtils;

/**
 * Standard format type.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum StandardFormatType implements EnumConst  {
	
	INTEGER("INT","!integerformat","######"),
	INTEGER_GROUPED("ING","!integerformat useGrouping:true","###,###"),
	DECIMAL("DEC","!decimalformat scale:2","######.00"),
	DECIMAL_GROUPED("DEG","!decimalformat scale:2 useGrouping:true","###,###.00"),
	DATE_DDMMYYYY_SLASH("DDS","!fixeddatetimeformat pattern:$s{dd/MM/yyyy}","dd/MM/yyyy"),
	DATE_MMDDYYYY_SLASH("DMS","!fixeddatetimeformat pattern:$s{MM/dd/yyyy}","MM/dd/yyyy"),
	DATE_YYYYMMDD_SLASH("DYS","!fixeddatetimeformat pattern:$s{yyyy/MM/dd}","yyyy/MM/dd"),
	DATE_DDMMYYYY_DASH("DDD","!fixeddatetimeformat pattern:$s{dd-MM-yyyy}","dd-MM-yyyy"),
	DATE_MMDDYYYY_DASH("DMD","!fixeddatetimeformat pattern:$s{MM-dd-yyyy}","MM-dd-yyyy"),
	DATE_YYYYMMDD_DASH("DYD","!fixeddatetimeformat pattern:$s{yyyy-MM-dd}","yyyy-MM-dd"),
	DATETIME_DDMMYYYY_SLASH("TDS","!fixeddatetimeformat pattern:$s{dd/MM/yyyy HH:mm:ss}","dd/MM/yyyy HH:mm:ss"),
	DATETIME_MMDDYYYY_SLASH("TMS","!fixeddatetimeformat pattern:$s{MM/dd/yyyy HH:mm:ss}","MM/dd/yyyy HH:mm:ss"),
	DATETIME_YYYYMMDD_SLASH("TYS","!fixeddatetimeformat pattern:$s{yyyy/MM/dd HH:mm:ss}","yyyy/MM/dd HH:mm:ss"),
	DATETIME_DDMMYYYY_DASH("TDD","!fixeddatetimeformat pattern:$s{dd-MM-yyyy HH:mm:ss}","dd-MM-yyyy HH:mm:ss"),
	DATETIME_MMDDYYYY_DASH("TMD","!fixeddatetimeformat pattern:$s{MM-dd-yyyy HH:mm:ss}","MM-dd-yyyy HH:mm:ss"),
	DATETIME_YYYYMMDD_DASH("TYD","!fixeddatetimeformat pattern:$s{yyyy-MM-dd HH:mm:ss}","yyyy-MM-dd HH:mm:ss");
	
	private final String code;
	
	private final String formatter;
	
	private final String label;

	private StandardFormatType(String code, String formatter, String label) {
		this.code = code;
		this.formatter = formatter;
		this.label = label;
	}

    @Override
	public String code() {
		return code;
	}

    @Override
    public String defaultCode() {
        return INTEGER.code;
    }

	public String formatter() {
		return formatter;
	}

	public String label() {
		return label;
	}

    public static StandardFormatType fromCode(String code) {
        return EnumUtils.fromCode(StandardFormatType.class, code);
    }

    public static StandardFormatType fromName(String name) {
        return EnumUtils.fromName(StandardFormatType.class, name);
    }
	
}

