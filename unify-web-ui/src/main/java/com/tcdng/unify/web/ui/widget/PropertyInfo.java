/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.widget;

/**
 * Property information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class PropertyInfo {

    private String property;

    private String shortProperty;

    private String longProperty;

    private boolean masked;

    public PropertyInfo(String property, String shortProperty, String longProperty, boolean masked) {
        this.property = property;
        this.shortProperty = shortProperty;
        this.longProperty = longProperty;
        this.masked = masked;
    }

    public PropertyInfo(String property) {
        this.property = property;
        this.shortProperty = property;
        this.longProperty = property;
        this.masked = false;
    }

    public String getProperty() {
        return property;
    }

    public String getShortProperty() {
        return shortProperty;
    }

    public String getLongProperty() {
        return longProperty;
    }

    public boolean isMasked() {
        return masked;
    }
}
