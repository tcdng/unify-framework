/*
 * Copyright 2018-2022 The Code Department.
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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Transactional;

/**
 * Another mock business service.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Transactional
@Component("anothermockservice")
public class AnotherMockServiceImpl extends AbstractBusinessService implements AnotherMockService {

    @Override
    public Long createLoanAccount(LoanAccount loanAccount) throws UnifyException {
        return (Long) db().create(loanAccount);
    }

    @Override
    public LoanAccount findLoanAccount(Long loanAccountId) throws UnifyException {
        return db().list(LoanAccount.class, loanAccountId);
    }

    @Override
    public List<LoanDisbursement> findLoanDisbursements(LoanDisbursementQuery query) throws UnifyException {
        return db().findAll(query);
    }

}
