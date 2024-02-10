/*
 * Copyright 2018-2024 The Code Department.
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

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Abstract base class for chart generators.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractChartGenerator extends AbstractUnifyComponent implements ChartGenerator {

    private Map<Class<? extends Chart>, ChartGeneratorUnit<Chart>> generatorUnits;

    public AbstractChartGenerator() {
        generatorUnits = new HashMap<Class<? extends Chart>, ChartGeneratorUnit<Chart>>();
    }
    
    @Override
    public byte[] generateImage(Chart chart) throws UnifyException {
        return getChartGeneratorUnit(chart).generateBitmap(chart);
    }

    @Override
    public void generateToFile(Chart chart, String filename) throws UnifyException {
        getChartGeneratorUnit(chart).generateToFile(chart, filename);
    }

    @Override
    public void generateToFile(Chart chart, File file) throws UnifyException {
        getChartGeneratorUnit(chart).generateToFile(chart, file);
    }

    @Override
    public void generateToStream(Chart chart, OutputStream outputStream) throws UnifyException {
        getChartGeneratorUnit(chart).generateToStream(chart, outputStream);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onInitialize() throws UnifyException {
        for (String name : getProvidedChartGeneratorUnitNames()) {
            ChartGeneratorUnit<Chart> generatorUnit = (ChartGeneratorUnit<Chart>) getComponent(name);
            Class<? extends Chart> chartType = generatorUnit.getChartType();
            if (generatorUnits.containsKey(chartType)) {
                throw new UnifyException(UnifyCoreErrorConstants.CHARTGENERATOR_MULTIPLE_GENERATOR_UNIT, getName(),
                        chartType);
            }

            generatorUnits.put(chartType, generatorUnit);
        }
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected ChartGeneratorUnit<Chart> getChartGeneratorUnit(Chart chart) throws UnifyException {
        ChartGeneratorUnit<Chart> generatorUnit = generatorUnits.get(chart.getClass());
        if (generatorUnit == null) {
            throw new UnifyException(UnifyCoreErrorConstants.CHARTGENERATOR_NO_GENERATOR_UNIT, getName(),
                    chart.getClass());
        }

        return generatorUnit;
    }

    /**
     * Gets the names of chart generator units provided by this chart generator.
     * 
     * @return the names of chart generator units
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract String[] getProvidedChartGeneratorUnitNames() throws UnifyException;
}
