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
package com.tcdng.unify.core.format;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.data.SimpleDateFormatPool;

/**
 * Date time format.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DateTimeFormat {

    private SimpleDateFormatPool sdfPool;

    private String subPattern;

    private List<? extends Listable> list;

    private int[] range;

    public DateTimeFormat(String subPattern, Locale locale, List<? extends Listable> list, int[] range) {
        sdfPool = new SimpleDateFormatPool(subPattern, locale);
        this.subPattern = subPattern;
        if (list != null) {
            this.list = Collections.unmodifiableList(list);
        }
        this.range = range;
    }

    public String getSubPattern() {
        return subPattern;
    }

    public List<? extends Listable> getList() {
        return list;
    }

    public int[] getRange() {
        return range;
    }

    public String[] getSubPatternKeys() {
        String[] values = new String[list.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i).getListKey();
        }
        return values;
    }

    public String[] getSubPatternDescriptions() {
        String[] values = new String[list.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = list.get(i).getListDescription();
        }
        return values;
    }

    public String format(Date date) throws UnifyException {
        SimpleDateFormat sdf = sdfPool.borrowObject();
        try {
            return sdf.format(date);
        } finally {
            sdfPool.returnObject(sdf);
        }
    }
}
