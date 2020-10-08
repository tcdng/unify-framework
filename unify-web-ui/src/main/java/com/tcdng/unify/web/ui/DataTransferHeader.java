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

import com.tcdng.unify.web.ui.widget.PropertyInfo;

/**
 * Data transfer header.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DataTransferHeader {

    private DataTransferBlock siblingBlock;

    private Object value;

    private String longName;

    private PropertyInfo propertyInfo;

    public DataTransferHeader(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public Object getDebugValue() {
        if (propertyInfo != null && propertyInfo.isMasked()) {
            return "********";
        }

        return value;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public PropertyInfo getBindingInfo() {
        return propertyInfo;
    }

    public void setBindingInfo(PropertyInfo propertyInfo) {
        this.propertyInfo = propertyInfo;
    }

    public String getLongProperty() {
        if (propertyInfo != null) {
            return propertyInfo.getLongProperty();
        }

        return null;
    }

    public String getShortProperty() {
        if (propertyInfo != null) {
            return propertyInfo.getShortProperty();
        }

        return null;
    }

    public DataTransferBlock getSiblingBlock() {
        return siblingBlock;
    }

    public void setSiblingBlock(DataTransferBlock siblingBlock) {
        this.siblingBlock = siblingBlock;
    }
}
