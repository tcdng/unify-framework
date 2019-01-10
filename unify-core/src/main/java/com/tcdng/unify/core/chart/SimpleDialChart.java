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
 * Simple dial chart.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SimpleDialChart extends AbstractChart {

    private String seriesName;

    private Double value;

    private SimpleDialChart(int width, int height, ChartBitmapFormat format, String seriesName, Double value) {
        super(width, height, format);
        this.seriesName = seriesName;
        this.value = value;
    }

    private SimpleDialChart(int width, int height, String seriesName, Double value) {
        super(width, height);
        this.seriesName = seriesName;
        this.value = value;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public Double getValue() {
        return value;
    }

    public static Builder newBuilder(int width, int height) {
        return new Builder(width, height);
    }

    public static class Builder {

        private int width;

        private int height;

        private ChartBitmapFormat format;

        private String seriesName;

        private Double value;

        public Builder(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public Builder format(ChartBitmapFormat format) {
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
