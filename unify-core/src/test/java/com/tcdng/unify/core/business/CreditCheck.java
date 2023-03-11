/*
 * Copyright 2018-2023 The Code Department.
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
 * Credit check.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Policy("creditcheck-policy")
@Table(datasource = "thirdparty-datasource", name = "CREDIT_CHECK", adhoc =  true)
public class CreditCheck extends AbstractEntity {

    @Id(name = "ACCOUNT_NM")
    private String accountName;

    @Column(name = "ACCOUNT_NO")
    private String accountNo;

    @Column(name = "LOAN_AMOUNT")
    private BigDecimal loanAmount;

    public CreditCheck(String accountName, String accountNo, BigDecimal loanAmount) {
        this.accountName = accountName;
        this.accountNo = accountNo;
        this.loanAmount = loanAmount;
    }

    public CreditCheck() {

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

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

}
