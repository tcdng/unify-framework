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
package com.tcdng.unify.core.report;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Reports format util.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class ReportFormatUtils {

    private static ReportFormatterStore reportFormatterStore;

    private ReportFormatUtils() {

    }

    public static void setReportFormatterStore(ReportFormatterStore reportFormatterStore) {
        ReportFormatUtils.reportFormatterStore = reportFormatterStore;
    }

    public static Formatter<Object> getFormatter(String formatterUpl) throws UnifyException {
        if (StringUtils.isNotBlank(formatterUpl)) {
            return reportFormatterStore.getFormatter(formatterUpl);
        }
        
        return null;
    }
    
    public static String format(String formatterUpl, Object value) throws UnifyException {
        return DataUtils.convert(String.class, value, getFormatter(formatterUpl));
    }
}
