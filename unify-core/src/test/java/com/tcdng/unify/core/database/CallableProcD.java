/*
 * Copyright (c) 2018-2025 The Code Department.
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
import java.util.Date;

import com.tcdng.unify.core.annotation.Callable;
import com.tcdng.unify.core.annotation.CallableDataType;
import com.tcdng.unify.core.annotation.InOutParam;
import com.tcdng.unify.core.annotation.InParam;
import com.tcdng.unify.core.annotation.OutParam;


/**
 * Test callable procedure with parameters and results.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Callable(
        procedure = "procedure_d",
        params = "itemId, startDt, endDt, accountName, balance",
        results = { CallableResultA.class })
public class CallableProcD extends AbstractCallableProc {

    @InParam
    private Integer itemId;

    @InParam(CallableDataType.TIMESTAMP)
    private Date startDt;

    @InParam
    private Date endDt;

    @InOutParam
    private String accountName;

    @OutParam
    private BigDecimal balance;

    public CallableProcD(Integer itemId, Date startDt, Date endDt, String accountName) {
        this.itemId = itemId;
        this.startDt = startDt;
        this.endDt = endDt;
        this.accountName = accountName;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Date getStartDt() {
        return startDt;
    }

    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

    public Date getEndDt() {
        return endDt;
    }

    public void setEndDt(Date endDt) {
        this.endDt = endDt;
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

}
