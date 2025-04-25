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

package com.tcdng.unify.core.chart;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Abstract base class for chart generator units.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractChartGeneratorUnit<T extends Chart> extends AbstractUnifyComponent
        implements ChartGeneratorUnit<T> {

    private Class<T> chartType;
    
    public AbstractChartGeneratorUnit(Class<T> chartType) {
        this.chartType = chartType;
    }

    @Override
    public Class<T> getChartType() {
        return chartType;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
