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
package com.tcdng.unify.core.database.dynamic;

import java.math.BigDecimal;

import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.Id;
import com.tcdng.unify.core.annotation.Policy;
import com.tcdng.unify.core.database.AbstractEntity;

/**
 * Account details record.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Policy("accountdetails-policy")
@Table(name = "ACCOUNT_DETAILS", adhoc = true)
public class AccountDetails extends AbstractEntity {

    @Id(name = "ACCOUNT_NM")
    private String accountName;

    @Column(name = "ACCOUNT_NO")
    private String accountNo;

    @Column(name = "AVAILABLE_BAL")
    private BigDecimal availBal;

    public AccountDetails(String accountName, String accountNo, BigDecimal availBal) {
        this.accountName = accountName;
        this.accountNo = accountNo;
        this.availBal = availBal;
    }

    public AccountDetails() {

    }

    @Override
    public Object getId() {
        return accountName;
    }

    @Override
    public String getDescription() {
        return accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getAvailBal() {
        return availBal;
    }

    public void setAvailBal(BigDecimal availBal) {
        this.availBal = availBal;
    }
}
