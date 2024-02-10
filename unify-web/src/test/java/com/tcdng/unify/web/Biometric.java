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
package com.tcdng.unify.web;

import com.tcdng.unify.core.data.AbstractDocument;

/**
 * Biometric data
 * 
 * @author The Code Department
 * @version 1.0
 */
public class Biometric extends AbstractDocument {

    private String marker;

    private Double height;

    public Biometric(String marker, Double height) {
        this.marker = marker;
        this.height = height;
    }

    public Biometric() {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Object getId() {
        return null;
    }

    @Override
    public String getBranchCode() {
        return null;
    }

    @Override
    public String getDepartmentCode() {
        return null;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
}
