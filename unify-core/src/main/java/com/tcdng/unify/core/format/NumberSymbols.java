/*
 * Copyright 2018-2019 The Code Department.
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

/**
 * Number symbols data object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class NumberSymbols {

    private NumberType numberType;

    private String negativePrefix;

    private String negativeSuffix;

    private String positivePrefix;

    private String positiveSuffix;

    private int groupSize;

    private char groupingSeparator;

    private char decimalSeparator;

    public NumberSymbols(NumberType numberType, String negativePrefix, String negativeSuffix, String positivePrefix,
            String positiveSuffix, int groupSize, char groupingSeparator, char decimalSeparator) {
        this.numberType = numberType;
        this.negativePrefix = negativePrefix;
        this.negativeSuffix = negativeSuffix;
        this.positivePrefix = positivePrefix;
        this.positiveSuffix = positiveSuffix;
        this.groupSize = groupSize;
        this.groupingSeparator = groupingSeparator;
        this.decimalSeparator = decimalSeparator;
    }

    public NumberType getNumberType() {
        return numberType;
    }

    public String getNegativePrefix() {
        return negativePrefix;
    }

    public String getNegativeSuffix() {
        return negativeSuffix;
    }

    public String getPositivePrefix() {
        return positivePrefix;
    }

    public String getPositiveSuffix() {
        return positiveSuffix;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public char getGroupingSeparator() {
        return groupingSeparator;
    }

    public char getDecimalSeparator() {
        return decimalSeparator;
    }
}
