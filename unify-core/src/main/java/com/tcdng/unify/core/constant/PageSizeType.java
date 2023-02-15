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
package com.tcdng.unify.core.constant;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Page size type.
 * 
 * @author The Code Department
 * @since 1.0
 */
@StaticList(name = "pagesizetypelist", description="$m{staticlist.pagesizetypelist}")
public enum PageSizeType implements EnumConst {

	A5("A5"),
	A4("A4"),
	A3("A3"),
	B5("B5"),
	B4("B4"),
	JIS_B5("JIS-B5"),
	JIS_B4("JIS-B4"),
	LETTER("letter"),
	LEGAL("legal"),
	CUSTOM("custom");

    private final String code;

    private PageSizeType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return A4.code;
    }

    public boolean isCustom() {
    	return CUSTOM.equals(this);
    }
    
    public static PageSizeType fromCode(String code) {
        return EnumUtils.fromCode(PageSizeType.class, code);
    }

    public static PageSizeType fromName(String name) {
        return EnumUtils.fromName(PageSizeType.class, name);
    }
}
