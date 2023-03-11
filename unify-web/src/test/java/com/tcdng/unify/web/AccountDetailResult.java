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
package com.tcdng.unify.web;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.tcdng.unify.web.remotecall.RemoteCallResult;

/**
 * Test account detail result.
 * 
 * @author The Code Department
 * @since 1.0
 */
@XmlRootElement
@XmlType(propOrder = { "accountNo", "accountName", "balance" })
public class AccountDetailResult extends RemoteCallResult {

    private String accountNo;

    private String accountName;

    private BigDecimal balance;

    public AccountDetailResult(String accountNo, String accountName, BigDecimal balance) {
        this.accountNo = accountNo;
        this.accountName = accountName;
        this.balance = balance;
    }

    public AccountDetailResult() {

    }

    public String getAccountNo() {
        return accountNo;
    }

    @XmlElement
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    @XmlElement
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @XmlElement
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
