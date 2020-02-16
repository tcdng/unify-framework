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

package com.tcdng.unify.core.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.constant.ColorPalette;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Line chart.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class LineChart extends AbstractChart {

    private List<XYSeries> seriesList;

    private boolean showXAxisTicks;

    private boolean showYAxisTicks;

    private LineChart(int width, int height, ColorPalette colorPalette, ChartImageFormat format,
            AnnotationType annotationType, ValueFormat valueFormat, boolean showLegend, List<XYSeries> seriesList,
            boolean showXAxisTicks, boolean showYAxisTicks) {
        super(width, height, colorPalette, format, annotationType, valueFormat, showLegend);
        this.seriesList = seriesList;
        this.showXAxisTicks = showXAxisTicks;
        this.showYAxisTicks = showYAxisTicks;
    }

    private LineChart(int width, int height, ColorPalette colorPalette, AnnotationType annotationType,
            ValueFormat valueFormat, boolean showLegend, List<XYSeries> seriesList, boolean showXAxisTicks,
            boolean showYAxisTicks) {
        super(width, height, colorPalette, annotationType, valueFormat, showLegend);
        this.seriesList = seriesList;
        this.showXAxisTicks = showXAxisTicks;
        this.showYAxisTicks = showYAxisTicks;
    }

    public List<XYSeries> getSeriesList() {
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

        private List<XYSeries> seriesList;

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
            this.showXAxisTicks = false;
            this.showYAxisTicks = false;
            this.colorPalette = ColorPalette.DEFAULT;
            this.seriesList = new ArrayList<XYSeries>();
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
            seriesList.add(new XYSeries(seriesName, xValueList, yValueList));
            return this;
        }

        public Builder addSeries(String seriesName, List<?> xValueList, List<? extends Number> yValueList,
                Color color) {
            seriesList.add(new XYSeries(seriesName, xValueList, yValueList, color));
            return this;
        }

        public Builder addSeries(String seriesName, List<?> xValueList, List<? extends Number> yValueList,
                String hexColor) {
            seriesList.add(new XYSeries(seriesName, xValueList, yValueList, Color.decode(hexColor)));
            return this;
        }

        public LineChart build() {
            if (format == null) {
                return new LineChart(width, height, colorPalette, annotationType, valueFormat, showLegend,
                        DataUtils.unmodifiableList(seriesList), showXAxisTicks, showYAxisTicks);
            }

            return new LineChart(width, height, colorPalette, format, annotationType, valueFormat, showLegend,
                    DataUtils.unmodifiableList(seriesList), showXAxisTicks, showYAxisTicks);
        }
    }

}
