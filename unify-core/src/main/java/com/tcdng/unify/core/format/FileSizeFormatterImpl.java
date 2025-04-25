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
package com.tcdng.unify.core.format;

import java.text.NumberFormat;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Default implementation of a file size formatter.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(name = "filesizeformat", description = "$m{format.filesize}")
public class FileSizeFormatterImpl extends AbstractFormatter<Number> implements FileSizeFormatter {

    private static final String[] FILESIZE_SUFFIX = { "KB", "MB", "GB", "TB", "PB", "EB" };

    private NumberFormat nf;

    public FileSizeFormatterImpl() {
        super(Number.class);
    }

    @Override
    protected String doFormat(Number value) throws UnifyException {
        if (value == null) {
            return null;
        }
        if (value.longValue() < 0) {
            throwOperationErrorException(new Exception("Invalid file size - " + value));
        }
        if (value.longValue() < 1024) {
            return value + " Bytes";
        }
        double bytes = value.doubleValue();
        int index = (int) (Math.log(bytes) / Math.log(1024));
        return String.format("%s %s", getNumberFormat().format(bytes / Math.pow(1024, index)),
                FILESIZE_SUFFIX[index - 1]);
    }

    @Override
    protected Number doParse(String string) throws UnifyException {
        try {
            if (string != null) {
                string = string.toUpperCase();
                for (int i = 0; i < string.length(); i++) {
                    if (Character.isLetter(string.charAt(i))) {
                        double value = getNumberFormat().parse(string.substring(0, i).trim()).doubleValue();
                        String suffix = string.substring(i);
                        if ("KB".equals(suffix)) {
                            value *= 1024;
                        } else if ("MB".equals(suffix)) {
                            value *= 1024 * 1024;
                        } else if ("GB".equals(suffix)) {
                            value *= 1024 * 1024 * 1024;
                        } else if ("TB".equals(suffix)) {
                            value *= 1024 * 1024 * 1024 * 1024;
                        } else if ("PB".equals(suffix)) {
                            value *= 1024 * 1024 * 1024 * 1024 * 1024;
                        } else if ("EB".equals(suffix)) {
                            value *= 1024 * 1024 * 1024 * 1024 * 1024 * 1024;
                        } else {
                            throw new Exception("Invalid suffix - " + suffix);
                        }
                        return (long) value;
                    }
                }
                return (long) getNumberFormat().parse(string.trim()).doubleValue();
            }
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        return null;
    }

    @Override
    public String getPattern() throws UnifyException {
        return null;
    }

    private NumberFormat getNumberFormat() throws UnifyException {
        if (nf == null) {
            nf = NumberFormat.getNumberInstance(getLocale());
            nf.setMaximumFractionDigits(1);
            nf.setMinimumFractionDigits(1);
        }
        return nf;
    }
}
