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

import java.io.File;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Type used for generating a specific type of chart.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ChartGeneratorUnit<T extends Chart> extends UnifyComponent {

    /**
     * Gets the generator unit chart type.
     * 
     * @return the type of chart this unit generates
     */
    Class<T> getChartType();

    /**
     * Generates supplied chart and returns generated bitmap as byte array.
     * 
     * @param chart
     *            the chart to generate
     * @return the generated bitmap
     * @throws UnifyException
     *             if there is no generator unit for chart type. if an error occurs
     */
    byte[] generateBitmap(T chart) throws UnifyException;

    /**
     * Generates supplied chart and saves as specified file.
     * 
     * @param chart
     *            the chart to generate
     * @param filename
     *            the name of the file to generate to
     * @throws UnifyException
     *             if there is no generator unit for chart type. if an error occurs
     */
    void generateToFile(T chart, String filename) throws UnifyException;

    /**
     * Generates supplied chart and saves as specified file.
     * 
     * @param chart
     *            the chart to generate
     * @param file
     *            the file to generate to
     * @throws UnifyException
     *             if there is no generator unit for chart type. if an error occurs
     */
    void generateToFile(T chart, File file) throws UnifyException;

    /**
     * Generates supplied chart to supplied output stream.
     * 
     * @param chart
     *            the chart to generate
     * @param outputStream
     *            the output stream to generate to
     * @throws UnifyException
     *             if there is no generator unit for chart type. if an error occurs
     */
    void generateToStream(T chart, OutputStream outputStream) throws UnifyException;
}
