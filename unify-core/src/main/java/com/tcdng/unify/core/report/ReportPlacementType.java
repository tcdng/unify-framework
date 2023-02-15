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

package com.tcdng.unify.core.report;

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Report placement types.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum ReportPlacementType implements EnumConst {

	TEXT("TXT"),
	LINE("LNE"),
	RECTANGLE("REC"),
	IMAGE("IMG");

	private final String code;

	private ReportPlacementType(String code) {
		this.code = code;
	}

	@Override
	public String code() {
		return this.code;
	}

	@Override
	public String defaultCode() {
		return TEXT.code;
	}

	public boolean isText() {
		return TEXT.equals(this);
	}

	public boolean isLine() {
		return LINE.equals(this);
	}

	public boolean isRectangle() {
		return RECTANGLE.equals(this);
	}

	public boolean isImage() {
		return IMAGE.equals(this);
	}
	
	public static ReportPlacementType fromCode(String code) {
		return EnumUtils.fromCode(ReportPlacementType.class, code);
	}

	public static ReportPlacementType fromName(String name) {
		return EnumUtils.fromName(ReportPlacementType.class, name);
	}
}
