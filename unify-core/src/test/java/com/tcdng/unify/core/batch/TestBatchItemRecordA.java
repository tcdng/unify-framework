/*
 * Copyright 2018 The Code Department
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

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.UniqueConstraint;
import com.tcdng.unify.core.database.AbstractTestEntity;

/**
 * Test batch item record A.
 *
 * @author Lateef Ojulari
 * @since 1.0
 */
@Table(name = "TESTBATCHITEMA", uniqueConstraints = { @UniqueConstraint({ "accountNo" }) })
public class TestBatchItemRecordA extends AbstractTestEntity implements BatchItemRecord {

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
		return null;
	}

	@Override
	public void setBatchId(Object id) {

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
