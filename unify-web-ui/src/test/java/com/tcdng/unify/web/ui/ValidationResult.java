/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.web.ui;

import java.util.Map;

import com.tcdng.unify.web.ui.widget.data.ValidationInfo;

/**
 * Validation result for validation tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ValidationResult {

    private Map<String, ValidationInfo> validationInfo;

    private String[] pageNames;

    private boolean pass;

    public ValidationResult(String[] pageNames, boolean pass) {
        this.pageNames = pageNames;
        this.pass = pass;
    }

    public void setValidationInfo(Map<String, ValidationInfo> validationInfo) {
        this.validationInfo = validationInfo;
    }

    /**
     * Returns the validation code.
     * 
     * @param componentIndex
     *            the component index
     */
    public String validationCode(int componentIndex) {
        return this.validationInfo.get(pageNames[componentIndex]).getValidationCode();
    }

    public boolean isPass() {
        return pass;
    }

    public Map<String, ValidationInfo> getValidationInfo() {
        return validationInfo;
    }

    public String[] getPageNames() {
        return pageNames;
    }

}
