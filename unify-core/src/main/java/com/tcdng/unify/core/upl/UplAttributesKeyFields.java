/*
 * Copyright 2018-2025 The Code Department.
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
 * Encapsulates the fields in a UPL attributes key.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class UplAttributesKeyFields {

    private int uplType;

    private String componentName;

    private String longName;

    private String descriptor;

    public UplAttributesKeyFields(int uplType, String componentName, String longName, String descriptor) {
        this.uplType = uplType;
        this.componentName = componentName;
        this.longName = longName;
        this.descriptor = descriptor;
    }

    public int getUplType() {
        return uplType;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getLongName() {
        return longName;
    }

    public String getDescriptor() {
        return descriptor;
    }

}
