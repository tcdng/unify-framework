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

import org.tcdng.unify.xchart.XChartApplicationComponents;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.chart.AbstractChartGenerator;

/**
 * Implementation of a chart generator using XChart.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(XChartApplicationComponents.XCHART_CHARTGENERATOR)
public class XChartGenerator extends AbstractChartGenerator {

    private static final String[] GENERATOR_UNITS = { XChartApplicationComponents.XCHART_SIMPLEDIALGENERATOR,
            XChartApplicationComponents.XCHART_BARCHARTGENERATOR, XChartApplicationComponents.XCHART_PIECHARTGENERATOR,
            XChartApplicationComponents.XCHART_LINECHARTGENERATOR,
            XChartApplicationComponents.XCHART_AREACHARTGENERATOR };

    @Override
    protected String[] getProvidedChartGeneratorUnitNames() throws UnifyException {
        return GENERATOR_UNITS;
    }

}
