/*
 * Copyright (c) 2018-2025 The Code Department.
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

package com.tcdng.unify.core.chart;

import java.awt.Color;

/**
 * Single value series object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class SingleValueSeries {

    private String name;

    private Double value;

    private Color color;

    public SingleValueSeries(String name, Double value, Color color) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

    public SingleValueSeries(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }
}
