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

package org.tcdng.unify.xchart.generator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.tcdng.unify.xchart.XChartApplicationComponents;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.chart.BarChart;
import com.tcdng.unify.core.chart.CategorySeries;
import com.tcdng.unify.core.util.DataUtils;

/**
 * XChart bar chart generator.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(XChartApplicationComponents.XCHART_BARCHARTGENERATOR)
public class BarChartGeneratorUnit extends AbstractXChartGeneratorUnit<BarChart> {

    public BarChartGeneratorUnit() {
        super(BarChart.class);
    }

    @Override
    protected Chart<?, ?> translate(BarChart barChart) throws UnifyException {

        // Create Chart
        CategoryChart categoryChart = new CategoryChartBuilder().width(barChart.getWidth())
                .height(barChart.getHeight()).theme(ChartTheme.GGPlot2).build();

        // Customize Chart
        categoryChart.getStyler().setLegendVisible(barChart.isShowLegend());
        categoryChart.getStyler().setPlotBackgroundColor(Color.WHITE);
        categoryChart.getStyler().setChartBackgroundColor(Color.WHITE);
        categoryChart.getStyler().setDecimalPattern(valueFormatMapping.get(barChart.getValueFormat()));
        categoryChart.getStyler().setHasAnnotations(true);
        categoryChart.getStyler().setXAxisTicksVisible(barChart.isShowXAxisTicks());
        categoryChart.getStyler().setYAxisTicksVisible(barChart.isShowYAxisTicks());

        // Series
        List<Color> customColors = Collections.emptyList();
        boolean useCustomColors = barChart.getColorPalette().isCustom();
        if (useCustomColors) {
            customColors = new ArrayList<Color>();
        }

        for (CategorySeries series : barChart.getSeriesList()) {
            if (useCustomColors) {
                customColors.add(series.getColor());
            }

            categoryChart.addSeries(series.getName(), series.getXValueList(), series.getYValueList());
        }

        if (useCustomColors) {
            categoryChart.getStyler().setSeriesColors(DataUtils.toArray(Color.class, customColors));
        } else if (!barChart.getColorPalette().isDefault()) {
            List<Color> palette = barChart.getColorPalette().pallete();
            categoryChart.getStyler().setSeriesColors(DataUtils.toArray(Color.class, palette));
        }

        return categoryChart;
    }

}
