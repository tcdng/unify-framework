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

package org.tcdng.unify.xchart;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.chart.AbstractChart.AnnotationType;
import com.tcdng.unify.core.chart.AbstractChart.ValueFormat;
import com.tcdng.unify.core.chart.AreaChart;
import com.tcdng.unify.core.chart.BarChart;
import com.tcdng.unify.core.chart.ChartGenerator;
import com.tcdng.unify.core.chart.LineChart;
import com.tcdng.unify.core.chart.PieChart;
import com.tcdng.unify.core.chart.SimpleDialChart;
import com.tcdng.unify.core.constant.ColorPalette;

/**
 * Chart generator tests
 * 
 * @author The Code Department
 * @since 4.1
 */
public class ChartGeneratorTest extends AbstractUnifyComponentTest {

    @Test
    public void testGenerateSimpleDial() throws Exception {
        SimpleDialChart chart = SimpleDialChart.newBuilder(300, 200).seriesName("Speed").value(0.8).build();
        byte[] image = getChartGenerator().generateImage(chart);
        assertNotNull(image);
        assertTrue(image.length > 0);
    }

    @Test
    public void testGeneratePieChart() throws Exception {
        PieChart chart = PieChart.newBuilder(740, 480).addSeries("Lufthansa", 250.0).addSeries("Virgin Atlantic", 400.0)
                .addSeries("Aeroflot", 100.0).addSeries("Finnair", 300.0).addSeries("KLM", 200.0)
                .addSeries("Air France", 350.0).addSeries("Turkish Airlines", 120.0)
                .colorPalette(ColorPalette.BLUE_SCALE).annotationType(AnnotationType.LABEL_VALUE)
                .valueFormat(ValueFormat.AMOUNT).showLegend(true).build();
        byte[] image = getChartGenerator().generateImage(chart);
        assertNotNull(image);
        assertTrue(image.length > 0);
//        getChartGenerator().generateToFile(chart, "e:\\data\\ming.png");
    }

    @Test
    public void testGenerateBarChart() throws Exception {
        BarChart chart = BarChart.newBuilder(640, 380)
                .addSeries("All Docs",
                        Arrays.asList("Loan Disbursement Online", "Virgin Atlantic", "Customer Onboarding"),
                        Arrays.asList(20, 35, 10), "#abcdef")
                .addSeries("My Docs",
                        Arrays.asList("Loan Disbursement Online", "Virgin Atlantic", "Customer Onboarding"),
                        Arrays.asList(8, 4, 8), "#fedcba")
                .colorPalette(ColorPalette.CUSTOM).annotationType(AnnotationType.LABEL_VALUE)
                .valueFormat(ValueFormat.COUNT).showLegend(true).build();
        byte[] image = getChartGenerator().generateImage(chart);
        assertNotNull(image);
        assertTrue(image.length > 0);
        // getChartGenerator().generateToFile(chart, "e:\\data\\ming.png");
    }

    @Test
    public void testGenerateLineChart() throws Exception {
        LineChart chart = LineChart.newBuilder(640, 380)
                .addSeries("All Docs", Arrays.asList(3, 5, 8, 10), Arrays.asList(20, 35, 10, 12))
                .addSeries("My Docs", Arrays.asList(2, 6, 7, 10), Arrays.asList(8, 4, 8, 16))
                .colorPalette(ColorPalette.BLUE_SCALE).annotationType(AnnotationType.LABEL_VALUE)
                .valueFormat(ValueFormat.COUNT).showLegend(true).build();
        byte[] image = getChartGenerator().generateImage(chart);
        assertNotNull(image);
        assertTrue(image.length > 0);
        // getChartGenerator().generateToFile(chart, "e:\\data\\ming.png");
    }

    @Test
    public void testGenerateAreaChart() throws Exception {
        AreaChart chart = AreaChart.newBuilder(640, 380)
                .addSeries("All Docs", Arrays.asList(3, 5, 8, 10), Arrays.asList(20, 35, 10, 12))
                .addSeries("My Docs", Arrays.asList(2, 6, 7, 10), Arrays.asList(8, 4, 8, 16))
                .colorPalette(ColorPalette.DEFAULT).annotationType(AnnotationType.LABEL_VALUE)
                .valueFormat(ValueFormat.COUNT).showLegend(true).build();
        byte[] image = getChartGenerator().generateImage(chart);
        assertNotNull(image);
        assertTrue(image.length > 0);
        // getChartGenerator().generateToFile(chart, "e:\\data\\ming.png");
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

    private ChartGenerator getChartGenerator() throws Exception {
        return (ChartGenerator) getComponent(XChartApplicationComponents.XCHART_CHARTGENERATOR);
    }
}
