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
package com.tcdng.unify.core.batch;

import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.database.AbstractTestVersionedTableEntity;

/**
 * Test batch item record B.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table(name = "TESTBATCHITEMB")
public class TestBatchItemRecordB extends AbstractTestVersionedTableEntity implements BatchItemRecord {

    @ForeignKey(TestBatchRecordB.class)
    private Long batchId;

    @Column
    private String accountNo;

    @Column
    private String beneficiary;

    @Column
    private String currency;

    @Column
    private Double amount;

    @Override
    public Object getBatchId() {
        return batchId;
    }

    @Override
    public void setBatchId(Object batchId) {
        this.batchId = (Long) batchId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
