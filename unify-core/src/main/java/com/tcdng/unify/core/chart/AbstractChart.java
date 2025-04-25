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

package com.tcdng.unify.core.chart;

import com.tcdng.unify.core.constant.ColorPalette;

/**
 * Abstract base class for charts.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractChart implements Chart {

    public enum AnnotationType {
        VALUE,
        PERCENTAGE,
        LABEL,
        LABEL_VALUE,
        LABEL_PERCENTAGE
    }

    public enum ValueFormat {
        INTEGER,
        DECIMAL,
        AMOUNT,
        COUNT,
    }

    private int width;

    private int height;

    private ColorPalette colorPalette;

    private ChartImageFormat format;

    private AnnotationType annotationType;

    private ValueFormat valueFormat;

    private boolean showLegend;

    protected AbstractChart(int width, int height, ColorPalette colorPalette, AnnotationType annotationType,
            ValueFormat valueFormat, boolean showLegend) {
        this(width, height, colorPalette, ChartImageFormat.PNG, annotationType, valueFormat, showLegend);
    }

    protected AbstractChart(int width, int height, ColorPalette colorPalette, ChartImageFormat format,
            AnnotationType annotationType, ValueFormat valueFormat, boolean showLegend) {
        this.width = width;
        this.height = height;
        this.colorPalette = colorPalette;
        this.format = format;
        this.showLegend = showLegend;
        this.annotationType = annotationType;
        this.valueFormat = valueFormat;
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

    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    public ValueFormat getValueFormat() {
        return valueFormat;
    }

    public boolean isShowLegend() {
        return showLegend;
    }

}
