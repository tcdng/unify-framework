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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.tcdng.unify.xchart.XChartApplicationComponents;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.chart.PieChart;
import com.tcdng.unify.core.chart.SingleValueSeries;
import com.tcdng.unify.core.util.DataUtils;

/**
 * XChart pie chart generator.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(XChartApplicationComponents.XCHART_PIECHARTGENERATOR)
public class PieChartGeneratorUnit extends AbstractXChartGeneratorUnit<PieChart> {

    private static final Map<PieChart.AnnotationType, PieStyler.AnnotationType> annotationMapping;

    static {
        Map<PieChart.AnnotationType, PieStyler.AnnotationType> tempAnnotationMapping =
                new HashMap<PieChart.AnnotationType, PieStyler.AnnotationType>();
        tempAnnotationMapping.put(PieChart.AnnotationType.LABEL, PieStyler.AnnotationType.Label);
        tempAnnotationMapping.put(PieChart.AnnotationType.LABEL_PERCENTAGE, PieStyler.AnnotationType.LabelAndPercentage);
        tempAnnotationMapping.put(PieChart.AnnotationType.LABEL_VALUE, PieStyler.AnnotationType.LabelAndValue);
        tempAnnotationMapping.put(PieChart.AnnotationType.PERCENTAGE, PieStyler.AnnotationType.Percentage);
        tempAnnotationMapping.put(PieChart.AnnotationType.VALUE, PieStyler.AnnotationType.Value);
        annotationMapping = DataUtils.unmodifiableMap(tempAnnotationMapping);
    }

    public PieChartGeneratorUnit() {
        super(PieChart.class);
    }

    @Override
    protected Chart<?, ?> translate(PieChart chart) throws UnifyException {

        // Create Chart
        org.knowm.xchart.PieChart pieChart = new PieChartBuilder().width(chart.getWidth()).height(chart.getHeight())
                .theme(ChartTheme.GGPlot2).build();

        // Customize Chart
        pieChart.getStyler().setLegendVisible(chart.isShowLegend());
        pieChart.getStyler().setAnnotationDistance(1.2);
        pieChart.getStyler().setAnnotationType(annotationMapping.get(chart.getAnnotationType()));
        pieChart.getStyler().setPlotContentSize(chart.getPlotContentSize());
        pieChart.getStyler().setStartAngleInDegrees(45);
        pieChart.getStyler().setPlotBackgroundColor(Color.WHITE);
        pieChart.getStyler().setChartBackgroundColor(Color.WHITE);
        pieChart.getStyler().setDecimalPattern(valueFormatMapping.get(chart.getValueFormat()));
        pieChart.getStyler().setDrawAllAnnotations(true);

        // Series
        List<Color> customColors = Collections.emptyList();
        boolean useCustomColors = chart.getColorPalette().isCustom();
        if (useCustomColors) {
            customColors = new ArrayList<Color>();
        }

        for (SingleValueSeries series : chart.getSeriesList()) {
            if (useCustomColors) {
                customColors.add(series.getColor());
            }

            pieChart.addSeries(series.getName(), series.getValue());
        }

        if (useCustomColors) {
            pieChart.getStyler().setSeriesColors(DataUtils.toArray(Color.class, customColors));
        } else if(!chart.getColorPalette().isDefault()) {
            List<Color> palette = chart.getColorPalette().pallete();
            pieChart.getStyler().setSeriesColors(DataUtils.toArray(Color.class, palette));
        }

        return pieChart;
    }

}
