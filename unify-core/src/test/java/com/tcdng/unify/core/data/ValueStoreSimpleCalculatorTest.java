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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

/**
 * Value store simple calculator tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ValueStoreSimpleCalculatorTest {

    @Test
    public void testAdd() throws Exception {
        BigDecimal awardedClaimPaid = BigDecimal.valueOf(2);
        BigDecimal otherDeductions = BigDecimal.valueOf(3);
        BigDecimal auctioneerFee = BigDecimal.valueOf(5);
        ValueStore creditScoreValueStore = new BeanValueStore(
                new CreditScore(awardedClaimPaid, otherDeductions, auctioneerFee));

        ValueStoreSimpleCalculator calculator = new ValueStoreSimpleCalculator(creditScoreValueStore);
        calculator.add("awardedClaimPaid").add("otherDeductions").add("auctioneerFee");
        BigDecimal result = calculator.getResult();
        assertEquals(BigDecimal.valueOf(10), result);
    }

    @Test
    public void testSubtract() throws Exception {
        BigDecimal awardedClaimPaid = BigDecimal.valueOf(2);
        BigDecimal otherDeductions = BigDecimal.valueOf(3);
        BigDecimal auctioneerFee = BigDecimal.valueOf(5);
        ValueStore creditScoreValueStore = new BeanValueStore(
                new CreditScore(awardedClaimPaid, otherDeductions, auctioneerFee));

        ValueStoreSimpleCalculator calculator = new ValueStoreSimpleCalculator(creditScoreValueStore);
        calculator.subtract("awardedClaimPaid").subtract("otherDeductions").subtract("auctioneerFee");
        BigDecimal result = calculator.getResult();
        assertEquals(BigDecimal.valueOf(-10), result);
    }

    @Test
    public void testMultiply() throws Exception {
        BigDecimal awardedClaimPaid = BigDecimal.valueOf(2);
        BigDecimal otherDeductions = BigDecimal.valueOf(3);
        BigDecimal auctioneerFee = BigDecimal.valueOf(5);
        ValueStore creditScoreValueStore = new BeanValueStore(
                new CreditScore(awardedClaimPaid, otherDeductions, auctioneerFee));

        ValueStoreSimpleCalculator calculator = new ValueStoreSimpleCalculator(creditScoreValueStore, BigDecimal.ONE);
        calculator.multiply("awardedClaimPaid").multiply("otherDeductions").multiply("auctioneerFee");
        BigDecimal result = calculator.getResult();
        assertEquals(BigDecimal.valueOf(30), result);
    }

    @Test
    public void testDivide() throws Exception {
        BigDecimal awardedClaimPaid = BigDecimal.valueOf(1);
        BigDecimal otherDeductions = BigDecimal.valueOf(3);
        BigDecimal auctioneerFee = BigDecimal.valueOf(5);
        ValueStore creditScoreValueStore = new BeanValueStore(
                new CreditScore(awardedClaimPaid, otherDeductions, auctioneerFee));

        ValueStoreSimpleCalculator calculator = new ValueStoreSimpleCalculator(creditScoreValueStore,
                BigDecimal.valueOf(30));
        calculator.divide("awardedClaimPaid", RoundingMode.HALF_UP).divide("otherDeductions", RoundingMode.HALF_UP)
                .divide("auctioneerFee", RoundingMode.HALF_UP);
        BigDecimal result = calculator.getResult();
        assertEquals(BigDecimal.valueOf(2), result);
    }
}
