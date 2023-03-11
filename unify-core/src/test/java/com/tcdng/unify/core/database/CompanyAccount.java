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
package com.tcdng.unify.core.database;

import java.math.BigDecimal;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.TenantId;
import com.tcdng.unify.core.annotation.UniqueConstraint;
import com.tcdng.unify.core.annotation.UniqueConstraints;

/**
 * Test company account record.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table(name = "COMPANY_ACCOUNT")
@UniqueConstraints({@UniqueConstraint({ "accountNo" }), @UniqueConstraint({ "accountName" })})
public class CompanyAccount extends AbstractTestTableEntity {

	@TenantId
	@Column(nullable = true)
	private Long companyId;
	
	@Column
	private String accountNo;
	
	@Column
	private String accountName;
	
	@Column
	private BigDecimal balance;

	public CompanyAccount(Long companyId, String accountNo, String accountName, BigDecimal balance) {
		this.companyId = companyId;
		this.accountNo = accountNo;
		this.accountName = accountName;
		this.balance = balance;
	}

	public CompanyAccount(String accountNo, String accountName, BigDecimal balance) {
		this.accountNo = accountNo;
		this.accountName = accountName;
		this.balance = balance;
	}

	public CompanyAccount() {

	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
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
