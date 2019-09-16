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

import com.tcdng.unify.core.constant.ColorPalette;

/**
 * Abstract base class for charts.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractChart implements Chart {

    private int width;

    private int height;

    private ColorPalette colorPalette;

    private ChartImageFormat format;

    public AbstractChart(int width, int height, ColorPalette colorPalette) {
        this(width, height, colorPalette, ChartImageFormat.PNG);
    }

    public AbstractChart(int width, int height, ColorPalette colorPalette, ChartImageFormat format) {
        this.width = width;
        this.height = height;
        this.colorPalette = colorPalette;
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
    public ColorPalette getColorPalette() {
        return colorPalette;
    }

    @Override
    public ChartImageFormat getImageFormat() {
        return format;
    }

}
