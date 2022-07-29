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

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.tcdng.unify.core.UnifyException;

/**
 * Value store simple calculator
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ValueStoreSimpleCalculator {

    private ValueStore valueStore;

    private BigDecimal result;

    public ValueStoreSimpleCalculator(ValueStore valueStore) {
        this.valueStore = valueStore;
        this.result = BigDecimal.ZERO;
    }

    public ValueStoreSimpleCalculator(ValueStore valueStore, BigDecimal initial) {
        this.valueStore = valueStore;
        this.result = initial;
    }

    public ValueStoreSimpleCalculator add(String fieldName) throws UnifyException {
        BigDecimal fieldValue = valueStore.retrieve(BigDecimal.class, fieldName);
        result = result.add(fieldValue != null ? fieldValue : BigDecimal.ZERO);
        return this;
    }

    public ValueStoreSimpleCalculator subtract(String fieldName) throws UnifyException {
        BigDecimal fieldValue = valueStore.retrieve(BigDecimal.class, fieldName);
        result = result.subtract(fieldValue != null ? fieldValue : BigDecimal.ZERO);
        return this;
    }

    public ValueStoreSimpleCalculator multiply(String fieldName) throws UnifyException {
        BigDecimal fieldValue = valueStore.retrieve(BigDecimal.class, fieldName);
        result = result.multiply(fieldValue != null ? fieldValue : BigDecimal.ZERO);
        return this;
    }

    public ValueStoreSimpleCalculator divide(String fieldName, RoundingMode roundingMode) throws UnifyException {
        BigDecimal fieldValue = valueStore.retrieve(BigDecimal.class, fieldName);
        result = result.divide(fieldValue != null ? fieldValue : BigDecimal.ZERO, roundingMode);
        return this;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void clear() {
        result = BigDecimal.ZERO;
    }
}
