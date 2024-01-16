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

import com.tcdng.unify.core.util.StringUtils;

/**
 * Parameter configuration.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ParamConfig {

    private String paramName;

    private String paramDesc;

    private String editor;

    private Class<?> type;

    private int order;

    private boolean mandatory;

    public ParamConfig(Class<?> type, String paramName, String paramDesc, String editor, int order, boolean mandatory) {
        this.paramName = paramName;
        this.paramDesc = paramDesc;
        this.editor = editor;
        this.type = type;
        this.order = order;
        this.mandatory = mandatory;
    }

    public ParamConfig(Class<?> type, String paramName) {
        this.type = type;
        this.paramName = paramName;
    }

    public Class<?> getType() {
        return type;
    }

    public String getParamName() {
        return paramName;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public String getEditor() {
        return editor;
    }

    public boolean isWithEditor() {
        return !StringUtils.isBlank(editor);
    }

    public int getOrder() {
		return order;
	}

	public boolean isMandatory() {
        return mandatory;
    }

}