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
package com.tcdng.unify.core.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * A class for managing a pool of simple date format objects.
 * 
 * @author Lateef Ojulari
 */
public class SimpleDateFormatPool extends AbstractPool<SimpleDateFormat> {

    private String pattern;

    private Locale locale;

    /**
     * Constructs a simple date format pool using specified pattern and locale. The
     * pool is setup with a get timeout of 2 seconds, a minimum pool size of 1 and a
     * maximum of 4.
     * 
     * @param pattern
     *            the date pattern
     * @param locale
     *            the locale
     */
    public SimpleDateFormatPool(String pattern, Locale locale) {
        this(pattern, locale, 2000, 1, 4);
    }

    /**
     * Constructs a simple date format pool using specified pattern, locale,
     * timeout, minimum and maximum size.
     * 
     * @param pattern
     *            the date pattern
     * @param locale
     *            the locale
     * @param getTimeout
     *            the get request timeout in milliseconds
     * @param minSize
     *            the minimum pool size
     * @param maxSize
     *            the maximum pool size
     */
    public SimpleDateFormatPool(String pattern, Locale locale, long getTimeout, int minSize, int maxSize) {
        super(getTimeout, minSize, maxSize);
        this.pattern = pattern;
        this.locale = locale;
    }

    /**
     * Formats a date using a format object from this pool.
     * 
     * @param date
     *            the date to format
     * @return a formatted date string
     * @throws UnifyException
     *             if an error occurs
     */
    public String format(Date date) throws UnifyException {
        SimpleDateFormat sdf = this.borrowObject();
        try {
            return sdf.format(date);
        } finally {
            super.returnObject(sdf);
        }
    }

    /**
     * Parses a date string using a format object from this pool.
     * 
     * @param dateString
     *            the date string to parse
     * @return Date the parsed date
     * @throws UnifyException
     *             if an error occurs
     */
    public Date parse(String dateString) throws UnifyException {
        SimpleDateFormat sdf = this.borrowObject();
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, e);
        } finally {
            super.returnObject(sdf);
        }
    }

    @Override
    protected SimpleDateFormat createObject(Object... params) throws Exception {
        return new SimpleDateFormat(pattern, locale);
    }

    @Override
    protected void onGetObject(SimpleDateFormat object, Object... params) throws Exception {

    }

    @Override
    protected void destroyObject(SimpleDateFormat sdf) {

    }
}
