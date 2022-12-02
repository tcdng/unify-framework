/*
 * Copyright 2018-2022 The Code Department.
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

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.convert.constants.StandardFormatType;
import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.data.LocaleFactoryMaps;
import com.tcdng.unify.core.data.SimpleDateFormatPool;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of a format helper.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_FORMATHELPER)
public class FormatHelperImpl extends AbstractUnifyComponent implements FormatHelper {

    private static final Map<String, int[]> numberPatternRangeMap;

    private static final Set<String> wordPatternSet;

    private LocaleFactoryMaps<String, DateTimeFormat> localeDateTimeFormatMaps;

    private LocaleFactoryMaps<NumberType, NumberSymbols> localeNumberSymbolMaps;

    private FactoryMap<String, SimpleDateFormatPool> simpleDateFormatPoolMap;

    private FactoryMap<String, List<Pattern.Base>> datePatternMap;

    private FactoryMap<String, String> longYearDatePatternMap;

    static {
        numberPatternRangeMap = new HashMap<String, int[]>();
        numberPatternRangeMap.put("HH", new int[] { 0, 23 });
        numberPatternRangeMap.put("H", new int[] { 0, 23 });
        numberPatternRangeMap.put("kk", new int[] { 1, 24 });
        numberPatternRangeMap.put("k", new int[] { 1, 24 });
        numberPatternRangeMap.put("KK", new int[] { 0, 11 });
        numberPatternRangeMap.put("K", new int[] { 0, 11 });
        numberPatternRangeMap.put("hh", new int[] { 1, 12 });
        numberPatternRangeMap.put("h", new int[] { 1, 12 });
        numberPatternRangeMap.put("mm", new int[] { 0, 59 });
        numberPatternRangeMap.put("m", new int[] { 0, 59 });
        numberPatternRangeMap.put("ss", new int[] { 0, 59 });
        numberPatternRangeMap.put("s", new int[] { 0, 59 });

        wordPatternSet = new HashSet<String>();
        wordPatternSet.add("M");
        wordPatternSet.add("MM");
        wordPatternSet.add("EEE");
        wordPatternSet.add("a");
    }

    public FormatHelperImpl() {
        localeDateTimeFormatMaps = new LocaleFactoryMaps<String, DateTimeFormat>() {
            @Override
            protected DateTimeFormat createObject(Locale locale, String subPattern, Object... params) throws Exception {
                List<Listable> list = null;
                int[] range = null;
                if (numberPatternRangeMap.containsKey(subPattern)) {
                    range = Arrays.copyOf(numberPatternRangeMap.get(subPattern), 2);
                } else if ("M".equals(subPattern) || "MM".equals(subPattern)) {
                    list = new ArrayList<Listable>();
                    String[] months = DateFormatSymbols.getInstance(locale).getMonths();
                    for (int i = 0; i < 12; i++) {
                        String numberStr = StringUtils.padLeft(String.valueOf(i + 1), '0', subPattern.length());
                        list.add(new ListData(numberStr, months[i]));
                    }
                } else if ("EEE".equals(subPattern)) {
                    list = new ArrayList<Listable>();
                    String[] shortWeekDays = DateFormatSymbols.getInstance(locale).getShortWeekdays();
                    String[] weekDays = DateFormatSymbols.getInstance(locale).getWeekdays();
                    for (int i = 1; i <= 7; i++) {
                        list.add(new ListData(shortWeekDays[i], weekDays[i]));
                    }
                } else if ("a".equals(subPattern)) {
                    list = new ArrayList<Listable>();
                    DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
                    for (String ampmStr : dfs.getAmPmStrings()) {
                        list.add(new ListData(ampmStr, ampmStr));
                    }
                } else {
                    throw new UnifyException(UnifyCoreErrorConstants.UNSUPPORTED_TIME_PATTERN, subPattern);
                }
                return new DateTimeFormat(subPattern, locale, list, range);
            }
        };

        localeNumberSymbolMaps = new LocaleFactoryMaps<NumberType, NumberSymbols>() {
            @Override
            protected NumberSymbols createObject(Locale locale, NumberType numberType, Object... params)
                    throws Exception {
                DecimalFormat df = null;
                switch (numberType) {
	                case INTEGER:
	                case INTEGER_ACCOUNTING:
                        df = (DecimalFormat) DecimalFormat.getIntegerInstance(locale);
                        break;
                    case PERCENT:
                        df = (DecimalFormat) DecimalFormat.getPercentInstance(locale);
                        break;
                    case DECIMAL:
	                case DECIMAL_ACCOUNTING:
                    default:
                        df = (DecimalFormat) DecimalFormat.getNumberInstance(locale);
                        break;
                }
                
                if (numberType.isAccounting()) {
                	df.setNegativePrefix("(");
                	df.setNegativeSuffix(")");
                }
                
                DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
                return new NumberSymbols(numberType, df.getNegativePrefix(), df.getNegativeSuffix(),
                        df.getPositivePrefix(), df.getPositiveSuffix(), df.getGroupingSize(),
                        String.valueOf(dfs.getGroupingSeparator()), String.valueOf(dfs.getDecimalSeparator()));
            }
        };

        simpleDateFormatPoolMap = new FactoryMap<String, SimpleDateFormatPool>() {

            @Override
            protected SimpleDateFormatPool create(String pattern, Object... params) throws Exception {
                return new SimpleDateFormatPool(pattern, Locale.getDefault());
            }
        };

        datePatternMap = new FactoryMap<String, List<Pattern.Base>>() {

            @Override
            protected List<Pattern.Base> create(String pattern, Object... params) throws Exception {
                List<Pattern.Base> result = new ArrayList<Pattern.Base>();
                int len = pattern.length();
                StringBuilder sb = new StringBuilder();
                boolean isQuoteBegun = false;
                boolean isFiller = false;
                for (int i = 0; i < len;) {
                    char ch = pattern.charAt(i);
                    i++;
                    if (ch == '\'') {
                        if (i < len && pattern.charAt(i) == '\'') {
                            if (!isFiller) {
                                result.add(new Pattern.Base(sb.toString(), isFiller));
                                sb = new StringBuilder();
                                isFiller = true;
                            }
                            sb.append(ch);
                            i++;
                        } else {
                            if (isQuoteBegun) {
                                result.add(new Pattern.Base(sb.toString(), true, true));
                                sb = new StringBuilder();
                                isQuoteBegun = false;
                            } else {
                                if (sb.length() > 0) {
                                    result.add(new Pattern.Base(sb.toString(), isFiller));
                                    sb = new StringBuilder();
                                }
                                isQuoteBegun = true;
                                isFiller = true;
                            }
                        }
                    } else if (Character.isLetter(ch) || Character.isDigit(ch)) {
                        if (isFiller && !isQuoteBegun) {
                            result.add(new Pattern.Base(sb.toString(), isFiller));
                            sb = new StringBuilder();
                            isFiller = false;
                        }
                        sb.append(ch);
                    } else {
                        if (!isFiller) {
                            result.add(new Pattern.Base(sb.toString(), isFiller));
                            sb = new StringBuilder();
                            isFiller = true;
                        }
                        sb.append(ch);
                    }
                }

                if (sb.length() > 0) {
                    result.add(new Pattern.Base(sb.toString(), isFiller));
                }
                return result;
            }
        };
        
        longYearDatePatternMap = new FactoryMap<String, String>() {

            @Override
            protected String create(String pattern, Object... params) throws Exception {
                List<Pattern.Base> list = datePatternMap.get(pattern);
                Pattern[] subPatterns = new Pattern[list.size()];
                for (int i = 0; i < subPatterns.length; i++) {
                    Pattern.Base base = list.get(i);
                    if (!base.isFiller() && base.getPattern().charAt(0) == 'y') {
                        subPatterns[i] = new Pattern(new Pattern.Base("yyyy", false));
                    } else {
                        subPatterns[i] = new Pattern(base);
                    }
                }

                return reconstructDatePattern(subPatterns);
            }
            
        };
    }

    @Override
    public NumberSymbols getNumberSymbols(NumberType numberType, Locale locale) throws UnifyException {
        return localeNumberSymbolMaps.get(locale, numberType);
    }

    @Override
    public Pattern[] splitDatePattern(String pattern) throws UnifyException {
        List<Pattern.Base> list = datePatternMap.get(pattern);
        Pattern[] result = new Pattern[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Pattern(list.get(i));
        }

        return result;
    }

    @Override
    public String reconstructDatePattern(Pattern[] subPatterns) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        for (Pattern p : subPatterns) {
            if (p.isQuoted()) {
                sb.append('\'');
            }

            String pattern = p.getPattern();
            int len = pattern.length();
            for (int i = 0; i < len; i++) {
                char ch = pattern.charAt(i);
                sb.append(ch);
                if (ch == '\'') {
                    sb.append(ch);
                }
            }

            if (p.isQuoted()) {
                sb.append('\'');
            }
        }
        return sb.toString();
    }

    @Override
    public DateTimeFormat getSubPatternDateTimeFormat(String pattern, Locale locale) throws UnifyException {
        return localeDateTimeFormatMaps.get(locale, pattern);
    }

    @Override
    public boolean isSupportedDateTimeSubPattern(String subPattern) throws UnifyException {
        return numberPatternRangeMap.containsKey(subPattern) || wordPatternSet.contains(subPattern);
    }

    @Override
    public String getDatePatternWithLongYear(String pattern) throws UnifyException {
        return longYearDatePatternMap.get(pattern);
    }

    @Override
    public String formatNow(String pattern) throws UnifyException {
        return format(pattern, new Date());
    }

    @Override
    public String format(String pattern, Date date) throws UnifyException {
        return simpleDateFormatPoolMap.get(pattern).format(date);
    }

    @Override
	public Formatter<?> newFormatter(String standardTypeCode) throws UnifyException {
		StandardFormatType formatType = StandardFormatType.fromCode(standardTypeCode);
		if (formatType == null) {
			throw new IllegalArgumentException(
					"Could not resolve standard format type from code [" + standardTypeCode + "]");
		}

		return newFormatter(formatType);
	}

	@Override
	public Formatter<?> newFormatter(StandardFormatType formatType) throws UnifyException {
		return (Formatter<?>) getUplComponent(getApplicationLocale(), formatType.formatter(), false);
	}

	@Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
