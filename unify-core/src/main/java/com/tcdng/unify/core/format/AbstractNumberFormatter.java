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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.util.FormattingUtils;

/**
 * Abstract base class for a number formatter.
 * 
 * @author The Code Department
 * @since 1.0
 */
@UplAttributes({
		@UplAttribute(name = "precision", type = int.class), @UplAttribute(name = "scale", type = int.class),
        @UplAttribute(name = "useGrouping", type = boolean.class) })
public abstract class AbstractNumberFormatter<T extends Number> extends AbstractFormatter<T>
        implements NumberFormatter<T> {

    private NumberType type;

    private NumberFormat nf;

    private NumberSymbols numberSymbols;

    private int precision;

    private int scale;

    private boolean groupingUsed;

    private boolean strictFormat;
    
    private String pattern;

    public AbstractNumberFormatter(Class<T> dataType, NumberType type) {
        super(dataType);
        this.type = type;
    }

    @Override
    protected String doFormat(T value) throws UnifyException {
        return getNumberFormat().format(value);
    }

    @Override
    public String getPattern() throws UnifyException {
        getNumberFormat();
        return pattern;
    }

    @Override
    public int getPrecision() throws UnifyException {
        if (precision > 0) {
            return precision;
        }
        return getUplAttribute(int.class, "precision");
    }

    @Override
    public void setPrecision(int precision) {
        this.precision = precision;
        nf = null;
    }

    @Override
    public int getScale() throws UnifyException {
        if (scale > 0) {
            return scale;
        }
        return getUplAttribute(int.class, "scale");
    }

    @Override
    public void setScale(int scale) {
        this.scale = scale;
        nf = null;
    }

    @Override
    public boolean isGroupingUsed() throws UnifyException {
        if (groupingUsed) {
            return groupingUsed;
        }
        return getUplAttribute(boolean.class, "useGrouping");
    }

    @Override
    public void setGroupingUsed(boolean groupingUsed) {
        this.groupingUsed = groupingUsed;
        nf = null;
    }

    @Override
    public boolean isStrictFormat() throws UnifyException {
        return strictFormat || super.isStrictFormat();
    }

    @Override
    public void setStrictFormat(boolean strictFormat) {
        this.strictFormat = strictFormat;
        nf = null;
    }

    @Override
    public NumberSymbols getNumberSymbols() throws UnifyException {
        getNumberFormat();
        return numberSymbols;
    }

    protected String ensureParsable(String string) throws UnifyException {
    	NumberSymbols ns = getNumberSymbols();
		if (isGroupingUsed()) {
			string = FormattingUtils.makeParsableGroupedAmount(string, ns);
		}

		return FormattingUtils.makeParsableNegativeAmount(string, ns);
    }
    
    protected NumberFormat getNumberFormat() throws UnifyException {
        final NumberType _type = type.getAccounting(isGlobalAccounting());
        if (nf == null || (!_type.equals(numberSymbols.getNumberType()))) {
            pattern = null;
            Locale locale = getLocale();
            numberSymbols = getFormatHelper().getNumberSymbols(_type, locale);

            switch (_type) {
	            case INTEGER:
	            case INTEGER_ACCOUNTING:
                    nf = NumberFormat.getIntegerInstance(locale);
                    break;
                case PERCENT:
                    nf = NumberFormat.getPercentInstance(locale);
                    break;
                case DECIMAL:
                case DECIMAL_ACCOUNTING:
                default:
                    nf = NumberFormat.getNumberInstance(locale);
                    break;
            }

            DecimalFormat df = (DecimalFormat) nf;
        	df.setNegativePrefix(numberSymbols.getNegativePrefix());
        	df.setNegativeSuffix(numberSymbols.getNegativeSuffix());
            
            nf.setGroupingUsed(isGroupingUsed());
            if (_type.isInteger()) {
                df.setParseBigDecimal(false);
                if (getPrecision() > 0) {
                    nf.setMaximumIntegerDigits(getPrecision());
                } else if (strictFormat) {
                    nf.setMaximumIntegerDigits(0);
                }
            } else {
                df.setParseBigDecimal(true);
                int precision = getPrecision();
                int scale = getScale();
                if (precision > 0) {
                    if (scale > 0) {
                        precision -= scale;
                    }
                    if (precision > 0) {
                        df.setMaximumIntegerDigits(precision);
                    } else if (strictFormat) {
                        df.setMaximumIntegerDigits(0);
                    }
                }
                if (scale > 0) {
                    df.setMaximumFractionDigits(scale);
                    df.setMinimumFractionDigits(scale);
                } else if (strictFormat) {
                    df.setMaximumFractionDigits(0);
                    df.setMinimumFractionDigits(0);
                }
            }

            pattern = df.toPattern();
        }
        return nf;
    }
}
