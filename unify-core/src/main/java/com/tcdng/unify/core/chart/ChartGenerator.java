/*
 * Copyright 2018-2020 The Code Department.
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
 * Component used for generating charts.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ChartGenerator extends UnifyComponent {

    /**
     * Generates supplied chart and returns generated image as byte array.
     * 
     * @param chart
     *            the chart to generate
     * @return the generated image
     * @throws UnifyException
     *             if there is no generator unit for chart type. if an error occurs
     */
    byte[] generateImage(Chart chart) throws UnifyException;

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
    void generateToFile(Chart chart, String filename) throws UnifyException;

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
    void generateToFile(Chart chart, File file) throws UnifyException;

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
    void generateToStream(Chart chart, OutputStream outputStream) throws UnifyException;
}
