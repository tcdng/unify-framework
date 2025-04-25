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
import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.constant.ColorPalette;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Pie Chart.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class PieChart extends AbstractChart {

    private static final double DEFAULT_PLOT_CONTENT_SIZE = 0.6;
    
    private List<SingleValueSeries> seriesList;

    private double plotContentSize;
    
    private PieChart(int width, int height, double plotContentSize, ColorPalette colorPalette, ChartImageFormat format,
            AnnotationType annotationType, ValueFormat valueFormat, boolean showLegend,
            List<SingleValueSeries> seriesList) {
        super(width, height, colorPalette, format, annotationType, valueFormat, showLegend);
        this.plotContentSize = plotContentSize;
        this.seriesList = seriesList;
    }

    private PieChart(int width, int height, double plotContentSize, ColorPalette colorPalette, AnnotationType annotationType,
            ValueFormat valueFormat, boolean showLegend, List<SingleValueSeries> seriesList) {
        super(width, height, colorPalette, annotationType, valueFormat, showLegend);
        this.plotContentSize = plotContentSize;
        this.seriesList = seriesList;
    }

    public List<SingleValueSeries> getSeriesList() {
        return seriesList;
    }

    public double getPlotContentSize() {
        return plotContentSize;
    }

    public static Builder newBuilder(int width, int height) {
        return new Builder(width, height);
    }

    public static class Builder {

        private int width;

        private int height;

        private double plotContentSize;

        private ChartImageFormat format;

        private List<SingleValueSeries> seriesList;

        private AnnotationType annotationType;

        private ValueFormat valueFormat;

        private ColorPalette colorPalette;

        private boolean showLegend;

        public Builder(int width, int height) {
            this.width = width;
            this.height = height;
            this.plotContentSize = DEFAULT_PLOT_CONTENT_SIZE;
            this.annotationType = AnnotationType.VALUE;
            this.valueFormat = ValueFormat.INTEGER;
            this.showLegend = true;
            this.colorPalette = ColorPalette.DEFAULT;
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

        public Builder plotContentSize(double plotContentSize) {
            this.plotContentSize = plotContentSize;
            return this;
        }

        public Builder showLegend(boolean showLegend) {
            this.showLegend = showLegend;
            return this;
        }

        public Builder colorPalette(ColorPalette colorPalette) {
            this.colorPalette = colorPalette;
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
                return new PieChart(width, height, plotContentSize, colorPalette, annotationType, valueFormat, showLegend,
                        DataUtils.unmodifiableList(seriesList));
            }

            return new PieChart(width, height, plotContentSize, colorPalette, format, annotationType, valueFormat, showLegend,
                    DataUtils.unmodifiableList(seriesList));
        }
    }

}
