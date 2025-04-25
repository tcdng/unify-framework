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
import java.util.List;

/**
 * Category series.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class CategorySeries {

    private String name;
    
    private Color color;
    
    private List<?> xValueList;
    
    private List<? extends Number> yValueList;

    public CategorySeries(String name, List<?> xValueList, List<? extends Number> yValueList, Color color) {
        this.name = name;
        this.xValueList = xValueList;
        this.yValueList = yValueList;
        this.color = color;
    }

    public CategorySeries(String name, List<?> xValueList, List<? extends Number> yValueList) {
        this.name = name;
        this.xValueList = xValueList;
        this.yValueList = yValueList;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public List<?> getXValueList() {
        return xValueList;
    }

    public List<? extends Number> getYValueList() {
        return yValueList;
    }
}
