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

package org.tcdng.unify.xchart.generator;

import java.awt.Color;

import org.knowm.xchart.DialChart;
import org.knowm.xchart.internal.chartpart.Chart;
import org.tcdng.unify.xchart.XChartApplicationComponents;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.chart.SimpleDialChart;
import com.tcdng.unify.core.chart.SingleValueSeries;

/**
 * XChart simple dial generator.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(XChartApplicationComponents.XCHART_SIMPLEDIALGENERATOR)
public class SimpleDialGeneratorUnit extends AbstractXChartGeneratorUnit<SimpleDialChart> {

    public SimpleDialGeneratorUnit() {
        super(SimpleDialChart.class);
    }

    @Override
    protected Chart<?, ?> translate(SimpleDialChart chart) throws UnifyException {
        DialChart dialChart = new DialChart(chart.getWidth(), chart.getHeight());
        SingleValueSeries series = chart.getSeries();
        dialChart.addSeries(series.getName(), series.getValue());
        dialChart.getStyler().setDonutThickness(0.4);
        dialChart.getStyler().setRedFrom(0.7);
        dialChart.getStyler().setArcAngle(240);
        dialChart.getStyler().setLegendVisible(false);
        dialChart.getStyler().setChartTitleBoxVisible(false);
        dialChart.getStyler().setChartPadding(0);
        dialChart.getStyler().setChartFontColor(Color.WHITE);
        dialChart.getStyler().setPlotBorderVisible(false);
        dialChart.getStyler().setAxisTitleVisible(false);
        dialChart.getStyler().setAxisTickLabelsVisible(false);
        dialChart.getStyler().setAxisTicksMarksVisible(true);
        dialChart.getStyler().setAxisTickMarksColor(Color.WHITE);
        return dialChart;
    }

}
