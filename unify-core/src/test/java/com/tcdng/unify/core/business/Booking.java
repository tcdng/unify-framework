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
package com.tcdng.unify.core.business;

import java.math.BigDecimal;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.Id;
import com.tcdng.unify.core.annotation.Policy;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.database.AbstractEntity;

/**
 * Booking.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Policy("booking-policy")
@Table
public class Booking extends AbstractEntity {

    @Id(name = "ACCOUNT_NM")
    private String accountName;

    @Column(name = "BOOKING_AMOUNT")
    private BigDecimal bookingAmount;

    public Booking(String accountName, BigDecimal bookingAmount) {
        this.accountName = accountName;
        this.bookingAmount = bookingAmount;
    }

    public Booking() {
        
    }
    
    @Override
    public Object getId() {
        return accountName;
    }

    @Override
    public String getDescription() {
        return accountName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getBookingAmount() {
        return bookingAmount;
    }

    public void setBookingAmount(BigDecimal bookingAmount) {
        this.bookingAmount = bookingAmount;
    }

}
