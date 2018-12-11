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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Plugin;

/**
 * Loan disbursement business logic unit.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Plugin(target = "anothermock-businessmodule", method = "createLoanAccount", paramTypes = { LoanAccount.class })
@Component("loandisbursement-logic")
public class LoanDisbursementLogicUnit extends AbstractBusinessLogicUnit {

	@Override
	public void execute(BusinessLogicInput input, BusinessLogicOutput output) throws UnifyException {
		LoanAccount loanAccount = input.getParameter(LoanAccount.class, "p0");
		if (loanAccount != null && loanAccount.getPrincipalAmount() > 100000.00) {
			LoanDisbursement loanDisbursement = new LoanDisbursement();
			loanDisbursement.setLoanAccountId(loanAccount.getId());
			loanDisbursement.setDisbursementCount(20);
			loanDisbursement.setDisbursementAmount(loanAccount.getPrincipalAmount());
			getDatabase(input).create(loanDisbursement);
		}
	}
}
