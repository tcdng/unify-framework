/*
 * Copyright 2018-2023 The Code Department.
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

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.tcdng.unify.xchart.XChartApplicationComponents;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.chart.LineChart;
import com.tcdng.unify.core.chart.XYSeries;
import com.tcdng.unify.core.util.DataUtils;

/**
 * XChart line chart generator.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(XChartApplicationComponents.XCHART_LINECHARTGENERATOR)
public class LineChartGeneratorUnit extends AbstractXChartGeneratorUnit<LineChart> {

    public LineChartGeneratorUnit() {
        super(LineChart.class);
    }

    @Override
    protected Chart<?, ?> translate(LineChart lineChart) throws UnifyException {

        // Create Chart
        XYChart xyChart = new XYChartBuilder().width(lineChart.getWidth()).height(lineChart.getHeight())
                .build();

        // Customize Chart
        xyChart.getStyler().setLegendVisible(lineChart.isShowLegend());
        xyChart.getStyler().setPlotBorderVisible(false);
        xyChart.getStyler().setPlotGridLinesVisible(false);
        xyChart.getStyler().setPlotBackgroundColor(Color.WHITE);
        xyChart.getStyler().setChartBackgroundColor(Color.WHITE);
        xyChart.getStyler().setDecimalPattern(valueFormatMapping.get(lineChart.getValueFormat()));
        xyChart.getStyler().setHasAnnotations(true);
        xyChart.getStyler().setXAxisTicksVisible(lineChart.isShowXAxisTicks());
        xyChart.getStyler().setYAxisTicksVisible(lineChart.isShowYAxisTicks());

        // Series
        List<Color> customColors = Collections.emptyList();
        boolean useCustomColors = lineChart.getColorPalette().isCustom();
        if (useCustomColors) {
            customColors = new ArrayList<Color>();
        }

        for (XYSeries series : lineChart.getSeriesList()) {
            if (useCustomColors) {
                customColors.add(series.getColor());
            }

            xyChart.addSeries(series.getName(), series.getXValueList(), series.getYValueList());
        }

        if (useCustomColors) {
            xyChart.getStyler().setSeriesColors(DataUtils.toArray(Color.class, customColors));
        } else if (!lineChart.getColorPalette().isDefault()) {
            List<Color> palette = lineChart.getColorPalette().pallete();
            xyChart.getStyler().setSeriesColors(DataUtils.toArray(Color.class, palette));
        }

        return xyChart;
    }

}
