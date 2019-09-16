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

package org.tcdng.unify.xchart;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.chart.ChartGenerator;
import com.tcdng.unify.core.chart.PieChart;
import com.tcdng.unify.core.chart.PieChart.AnnotationType;
import com.tcdng.unify.core.chart.PieChart.ValueFormat;
import com.tcdng.unify.core.chart.SimpleDialChart;
import com.tcdng.unify.core.constant.ColorPalette;

/**
 * Chart generator tests
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ChartGeneratorTest extends AbstractUnifyComponentTest {

    @Test
    public void testGenerateSimpleDial() throws Exception {
        SimpleDialChart chart =
                SimpleDialChart.newBuilder(300, 200).seriesName("Speed").value(0.8).build();
        byte[] image = getChartGenerator().generateImage(chart);
        assertNotNull(image);
        assertTrue(image.length > 0);
    }

    @Test
    public void testGeneratePieChart() throws Exception {
        PieChart chart =
                PieChart.newBuilder(640, 380)
                        .addSeries("Lufthansa", 250.0)
                        .addSeries("Virgin Atlantic", 400.0)
                        .addSeries("Aeroflot", 100.0)
                        .addSeries("Finnair", 300.0)
                        .addSeries("KLM", 200.0)
                        .addSeries("Air France", 350.0)
                        .addSeries("Turkish Airlines", 120.0)
                        .colorPalette(ColorPalette.BLUE_SCALE)
                        .annotationType(AnnotationType.LABEL_VALUE)
                        .valueFormat(ValueFormat.AMOUNT)
                        .showLegend(true)
                        .build();
        byte[] image = getChartGenerator().generateImage(chart);
        assertNotNull(image);
        assertTrue(image.length > 0);
        getChartGenerator().generateToFile(chart, "e:\\data\\ming.png");
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
