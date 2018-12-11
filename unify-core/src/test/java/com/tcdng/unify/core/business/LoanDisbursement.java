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
package com.tcdng.unify.core.business;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.database.AbstractTestEntity;

/**
 * Loan disbursement record.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Table
public class LoanDisbursement extends AbstractTestEntity {

	@ForeignKey(LoanAccount.class)
	private Long loanAccountId;

	@Column
	private int disbursementCount;

	@Column
	private Double disbursementAmount;

	public Long getLoanAccountId() {
		return loanAccountId;
	}

	public void setLoanAccountId(Long loanAccountId) {
		this.loanAccountId = loanAccountId;
	}

	public int getDisbursementCount() {
		return disbursementCount;
	}

	public void setDisbursementCount(int disbursementCount) {
		this.disbursementCount = disbursementCount;
	}

	public Double getDisbursementAmount() {
		return disbursementAmount;
	}

	public void setDisbursementAmount(Double disbursementAmount) {
		this.disbursementAmount = disbursementAmount;
	}
}
