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
 * Simple dial chart.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class SimpleDialChart extends AbstractChart {

    private SingleValueSeries singleValueSeries;

    private SimpleDialChart(int width, int height, ChartImageFormat format, String seriesName, Double value) {
        super(width, height, ColorPalette.DEFAULT, format, null, null, false);
        this.singleValueSeries = new SingleValueSeries(seriesName, value);
    }

    private SimpleDialChart(int width, int height, String seriesName, Double value) {
        super(width, height, ColorPalette.DEFAULT, null, null, false);
        this.singleValueSeries = new SingleValueSeries(seriesName, value);
    }

    public SingleValueSeries getSeries() {
        return singleValueSeries;
    }

    public static Builder newBuilder(int width, int height) {
        return new Builder(width, height);
    }

    public static class Builder {

        private int width;

        private int height;

        private ChartImageFormat format;

        private String seriesName;

        private Double value;

        public Builder(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public Builder format(ChartImageFormat format) {
            this.format = format;
            return this;
        }

        public Builder seriesName(String seriesName) {
            this.seriesName = seriesName;
            return this;
        }

        public Builder value(Double value) {
            this.value = value;
            return this;
        }

        public SimpleDialChart build() {
            if (format == null) {
                return new SimpleDialChart(width, height, seriesName, value);
            }
            return new SimpleDialChart(width, height, format, seriesName, value);
        }
    }
}
