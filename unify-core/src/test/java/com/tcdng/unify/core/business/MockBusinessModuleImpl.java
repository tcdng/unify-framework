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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Synchronized;
import com.tcdng.unify.core.annotation.TransactionAttribute;
import com.tcdng.unify.core.annotation.Transactional;

/**
 * A mock business module.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Transactional
@Component("mock-businessmodule")
public class MockBusinessModuleImpl extends AbstractBusinessModule implements MockBusinessModule {

    @Configurable("anothermock-businessmodule")
    private AnotherMockBusinessModule anotherMockBusinessModule;

    @Override
    @Synchronized("sling")
    public int add(int a, int b) throws UnifyException {
        return a + b;
    }

    @Override
    @Transactional(TransactionAttribute.REQUIRES_NEW)
    public int multiply(int a, int b) throws UnifyException {
        return a * b;
    }

    @Override
    @Synchronized("sling")
    public String hello() throws UnifyException {
        return "Hello World!";
    }

    @Override
    public Long createAccount(Account account) throws UnifyException {
        return (Long) db().create(account);
    }

    @Override
    public Account findAccount(Long accountId) throws UnifyException {
        return db().find(Account.class, accountId);
    }

    @Override
    public List<Account> find(AccountQuery query) throws UnifyException {
        return db().listAll(query);
    }

    @Override
    public Long createLoanAccount(String accountNo, String accountName, Double amount) throws UnifyException {
        Long accountId = (Long) db().create(new Account(accountNo, accountName));
        return anotherMockBusinessModule.createLoanAccount(new LoanAccount(accountId, amount));
    }

    @Override
    public LoanAccount findLoanAccount(Long loanAccountId) throws UnifyException {
        return anotherMockBusinessModule.findLoanAccount(loanAccountId);
    }
}
