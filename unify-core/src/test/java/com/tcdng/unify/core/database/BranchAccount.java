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
package com.tcdng.unify.core.database;

import java.math.BigDecimal;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.Table;

/**
 * Test branch account record.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table(name = "BRANCH_ACCOUNT")
public class BranchAccount extends AbstractTestVersionedTableEntity {

    @ForeignKey(Branch.class)
    private Long branchId;
    
    @Column
    private String accountName;

    @Column
    private BigDecimal balance;

    @ListOnly(key = "branchId", property = "sortCode")
    private String sortCode;

    @ListOnly(key = "branchId", property = "state")
    private String state;
    
    public BranchAccount(Long branchId, String accountName, BigDecimal balance) {
        this.branchId = branchId;
        this.accountName = accountName;
        this.balance = balance;
    }

    public BranchAccount() {
        
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
