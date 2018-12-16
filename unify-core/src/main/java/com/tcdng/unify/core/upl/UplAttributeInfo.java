/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.upl;

/**
 * UPL attribute information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UplAttributeInfo {

    private Class<?> attributeClass;

    private String defaultValue;

    private boolean mandatory;

    public UplAttributeInfo(Class<?> attributeClass, String defaultValue, boolean mandatory) {
        this.attributeClass = attributeClass;
        this.defaultValue = defaultValue;
        this.mandatory = mandatory;
    }

    public Class<?> getAttributeClass() {
        return attributeClass;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
