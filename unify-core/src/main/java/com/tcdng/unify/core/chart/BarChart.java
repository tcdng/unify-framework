/*
 * Copyright 2018-2022 The Code Department.
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
import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.constant.ColorPalette;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Bar chart.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class BarChart extends AbstractChart {

    private List<CategorySeries> seriesList;

    private boolean showXAxisTicks;

    private boolean showYAxisTicks;

    private BarChart(int width, int height, ColorPalette colorPalette, ChartImageFormat format,
            AnnotationType annotationType, ValueFormat valueFormat, boolean showLegend, List<CategorySeries> seriesList,
            boolean showXAxisTicks, boolean showYAxisTicks) {
        super(width, height, colorPalette, format, annotationType, valueFormat, showLegend);
        this.seriesList = seriesList;
        this.showXAxisTicks = showXAxisTicks;
        this.showYAxisTicks = showYAxisTicks;
    }

    private BarChart(int width, int height, ColorPalette colorPalette, AnnotationType annotationType,
            ValueFormat valueFormat, boolean showLegend, List<CategorySeries> seriesList, boolean showXAxisTicks,
            boolean showYAxisTicks) {
        super(width, height, colorPalette, annotationType, valueFormat, showLegend);
        this.seriesList = seriesList;
        this.showXAxisTicks = showXAxisTicks;
        this.showYAxisTicks = showYAxisTicks;
    }

    public List<CategorySeries> getSeriesList() {
        return seriesList;
    }

    public boolean isShowXAxisTicks() {
        return showXAxisTicks;
    }

    public boolean isShowYAxisTicks() {
        return showYAxisTicks;
    }

    public static Builder newBuilder(int width, int height) {
        return new Builder(width, height);
    }

    public static class Builder {

        private int width;

        private int height;

        private ChartImageFormat format;

        private List<CategorySeries> seriesList;

        private AnnotationType annotationType;

        private ValueFormat valueFormat;

        private ColorPalette colorPalette;

        private boolean showLegend;

        private boolean showXAxisTicks;

        private boolean showYAxisTicks;

        public Builder(int width, int height) {
            this.width = width;
            this.height = height;
            this.annotationType = AnnotationType.VALUE;
            this.valueFormat = ValueFormat.INTEGER;
            this.showLegend = true;
            this.showXAxisTicks = true;
            this.showYAxisTicks = false;
            this.colorPalette = ColorPalette.DEFAULT;
            this.seriesList = new ArrayList<CategorySeries>();
        }

        public Builder format(ChartImageFormat format) {
            this.format = format;
            return this;
        }

        public Builder annotationType(AnnotationType annotationType) {
            this.annotationType = annotationType;
            return this;
        }

        public Builder valueFormat(ValueFormat valueFormat) {
            this.valueFormat = valueFormat;
            return this;
        }

        public Builder showLegend(boolean showLegend) {
            this.showLegend = showLegend;
            return this;
        }

        public Builder showYAxisTicks(boolean showYAxisTicks) {
            this.showYAxisTicks = showYAxisTicks;
            return this;
        }

        public Builder showXAxisTicks(boolean showXAxisTicks) {
            this.showXAxisTicks = showXAxisTicks;
            return this;
        }

        public Builder colorPalette(ColorPalette colorPalette) {
            this.colorPalette = colorPalette;
            return this;
        }

        public Builder addSeries(String seriesName, List<?> xValueList, List<? extends Number> yValueList) {
            seriesList.add(new CategorySeries(seriesName, xValueList, yValueList));
            return this;
        }

        public Builder addSeries(String seriesName, List<?> xValueList, List<? extends Number> yValueList,
                Color color) {
            seriesList.add(new CategorySeries(seriesName, xValueList, yValueList, color));
            return this;
        }

        public Builder addSeries(String seriesName, List<?> xValueList, List<? extends Number> yValueList,
                String hexColor) {
            seriesList.add(new CategorySeries(seriesName, xValueList, yValueList, Color.decode(hexColor)));
            return this;
        }

        public BarChart build() {
            if (format == null) {
                return new BarChart(width, height, colorPalette, annotationType, valueFormat, showLegend,
                        DataUtils.unmodifiableList(seriesList), showXAxisTicks, showYAxisTicks);
            }

            return new BarChart(width, height, colorPalette, format, annotationType, valueFormat, showLegend,
                    DataUtils.unmodifiableList(seriesList), showXAxisTicks, showYAxisTicks);
        }
    }

}
