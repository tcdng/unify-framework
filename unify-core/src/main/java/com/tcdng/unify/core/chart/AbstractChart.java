/*
 * Copyright 2018-2019 The Code Department.
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

/**
 * Abstract base class for charts.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractChart implements Chart {

    private int width;

    private int height;

    private boolean useCustomColors;

    private ChartImageFormat format;

    public AbstractChart(int width, int height, boolean useCustomColors) {
        this(width, height, useCustomColors, ChartImageFormat.PNG);
    }

    public AbstractChart(int width, int height, boolean useCustomColors, ChartImageFormat format) {
        this.width = width;
        this.height = height;
        this.useCustomColors = useCustomColors;
        this.format = format;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean isUseCustomColors() {
        return useCustomColors;
    }

    @Override
    public ChartImageFormat getImageFormat() {
        return format;
    }

}
