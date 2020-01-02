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

package org.tcdng.unify.xchart.generator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.chart.AbstractChartGeneratorUnit;
import com.tcdng.unify.core.chart.Chart;
import com.tcdng.unify.core.chart.ChartImageFormat;

/**
 * Abstract base class for XChart chart generator units.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractXChartGeneratorUnit<T extends Chart> extends AbstractChartGeneratorUnit<T> {

    private static final Map<ChartImageFormat, BitmapFormat> formats;

    static {
        Map<ChartImageFormat, BitmapFormat> tempFormats = new HashMap<ChartImageFormat, BitmapFormat>();
        tempFormats.put(ChartImageFormat.BMP, BitmapFormat.BMP);
        tempFormats.put(ChartImageFormat.GIF, BitmapFormat.GIF);
        tempFormats.put(ChartImageFormat.JPG, BitmapFormat.JPG);
        tempFormats.put(ChartImageFormat.PNG, BitmapFormat.PNG);
        formats = Collections.unmodifiableMap(tempFormats);
    }

    public AbstractXChartGeneratorUnit(Class<T> chartType) {
        super(chartType);
    }

    public byte[] generateBitmap(T chart) throws UnifyException {
        try {
            return BitmapEncoder.getBitmapBytes(translate(chart), getBitmapFormat(chart));
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
        return null;
    }

    public void generateToFile(T chart, String filename) throws UnifyException {
        try {
            BitmapEncoder.saveBitmap(translate(chart), filename, getBitmapFormat(chart));
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
    }

    public void generateToFile(T chart, File file) throws UnifyException {
        try {
            BitmapEncoder.saveBitmap(translate(chart), file.getAbsolutePath(), getBitmapFormat(chart));
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
    }

    public void generateToStream(T chart, OutputStream outputStream) throws UnifyException {
        try {
            BitmapEncoder.saveBitmap(translate(chart), outputStream, getBitmapFormat(chart));
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
    }

    protected BitmapFormat getBitmapFormat(T chart) {
        return formats.get(chart.getImageFormat());
    }

    protected abstract org.knowm.xchart.internal.chartpart.Chart<?, ?> translate(T chart) throws UnifyException;
}
