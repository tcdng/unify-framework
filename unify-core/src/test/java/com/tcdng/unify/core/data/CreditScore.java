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

/**
 * 
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class CreditScore {

    private BigDecimal awardedClaimPaid;

    private BigDecimal otherDeductions;

    private BigDecimal auctioneerFee;

    public CreditScore(BigDecimal awardedClaimPaid, BigDecimal otherDeductions, BigDecimal auctioneerFee) {
        this.awardedClaimPaid = awardedClaimPaid;
        this.otherDeductions = otherDeductions;
        this.auctioneerFee = auctioneerFee;
    }

    public BigDecimal getAwardedClaimPaid() {
        return awardedClaimPaid;
    }

    public BigDecimal getOtherDeductions() {
        return otherDeductions;
    }

    public BigDecimal getAuctioneerFee() {
        return auctioneerFee;
    }

}
