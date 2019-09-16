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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.util.DataUtils;

/**
 * Pie Chart.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PieChart extends AbstractChart {

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

    private List<SingleValueSeries> seriesList;

    private AnnotationType annotationType;

    private ValueFormat valueFormat;

    private boolean showLegend;

    private PieChart(int width, int height, boolean showLegend, boolean useCustomColors, ChartImageFormat format,
            AnnotationType annotationType, ValueFormat valueFormat, List<SingleValueSeries> seriesList) {
        super(width, height, useCustomColors, format);
        this.showLegend = showLegend;
        this.annotationType = annotationType;
        this.valueFormat = valueFormat;
        this.seriesList = seriesList;
    }

    private PieChart(int width, int height, boolean showLegend, boolean useCustomColors, AnnotationType annotationType,
            ValueFormat valueFormat, List<SingleValueSeries> seriesList) {
        super(width, height, useCustomColors);
        this.showLegend = showLegend;
        this.annotationType = annotationType;
        this.valueFormat = valueFormat;
        this.seriesList = seriesList;
    }

    public List<SingleValueSeries> getSeriesList() {
        return seriesList;
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

    public static Builder newBuilder(int width, int height) {
        return new Builder(width, height);
    }

    public static class Builder {

        private int width;

        private int height;

        private ChartImageFormat format;

        private List<SingleValueSeries> seriesList;

        private AnnotationType annotationType;

        private ValueFormat valueFormat;

        private boolean showLegend;

        private boolean useCustomColors;

        public Builder(int width, int height) {
            this.width = width;
            this.height = height;
            this.annotationType = AnnotationType.VALUE;
            this.valueFormat = ValueFormat.INTEGER;
            this.showLegend = true;
            this.useCustomColors = false;
            this.seriesList = new ArrayList<SingleValueSeries>();
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

        public Builder useCustomColors(boolean useCustomColors) {
            this.useCustomColors = useCustomColors;
            return this;
        }

        public Builder addSeries(String seriesName, Double value) {
            seriesList.add(new SingleValueSeries(seriesName, value));
            return this;
        }

        public Builder addSeries(String seriesName, Double value, Color color) {
            seriesList.add(new SingleValueSeries(seriesName, value, color));
            return this;
        }

        public Builder addSeries(String seriesName, Double value, String hexColor) {
            seriesList.add(new SingleValueSeries(seriesName, value, Color.decode(hexColor)));
            return this;
        }

        public PieChart build() {
            if (format == null) {
                return new PieChart(width, height, showLegend, useCustomColors, annotationType, valueFormat,
                        DataUtils.unmodifiableList(seriesList));
            }

            return new PieChart(width, height, showLegend, useCustomColors, format, annotationType, valueFormat,
                    DataUtils.unmodifiableList(seriesList));
        }
    }

}
