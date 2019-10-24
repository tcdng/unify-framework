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

package com.tcdng.unify.core.data;

import java.math.BigDecimal;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Money object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Money {

    private String currencyCode;

    private BigDecimal amount;

    public Money(String currencyCode, BigDecimal amount) {
        this.currencyCode = currencyCode;
        this.amount = amount;
    }

    public Money(Money money) {
        this.currencyCode = money.currencyCode;
        this.amount = money.amount;
    }

    @Override
    public boolean equals(Object obj) {
        return ReflectUtils.beanEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return ReflectUtils.beanHashCode(this);
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Money add(Money addend) throws UnifyException {
        this.checkCurrency(addend);
        return new Money(this.currencyCode, this.amount.add(addend.getAmount()));
    }

    public Money subtract(Money subtrahend) throws UnifyException {
        this.checkCurrency(subtrahend);
        return new Money(this.currencyCode, this.amount.subtract(subtrahend.getAmount()));
    }

    public Money multiply(Money multiplicand) throws UnifyException {
        this.checkCurrency(multiplicand);
        return new Money(this.currencyCode, this.amount.multiply(multiplicand.getAmount()));
    }

    public Money divide(Money divisor) throws UnifyException {
        this.checkCurrency(divisor);
        return new Money(this.currencyCode, this.amount.divide(divisor.getAmount()));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.currencyCode != null) {
            sb.append(this.currencyCode);
        }

        if (this.amount != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }

            sb.append(this.amount);
        }

        return sb.toString();
    }

    private void checkCurrency(Money money) throws UnifyException {
        if (this.currencyCode == null || !this.currencyCode.equals(money.getCurrencyCode())) {
            throw new UnifyException(UnifyCoreErrorConstants.INCOMPATIBLE_MONEY_CURRENCY, this.currencyCode,
                    money.getCurrencyCode());
        }
    }
}
