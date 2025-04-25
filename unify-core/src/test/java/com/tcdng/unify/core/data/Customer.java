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
package com.tcdng.unify.core.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.tcdng.unify.core.constant.Gender;

/**
 * Test customer bean.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class Customer extends AbstractDocument {

    private String name;

    private Date birthDt;

    private BigDecimal balance;

    private Long id;

    private Address address;

    private List<String> modeList;

    private Gender gender;
    
    public Customer(String name, Date birthDt, BigDecimal balance, long id, Address address, Gender gender) {
        this.name = name;
        this.birthDt = birthDt;
        this.balance = balance;
        this.id = id;
        this.address = address;
        this.gender = gender;
    }

    public Customer(String name, Date birthDt, BigDecimal balance, Long id, Gender gender) {
        this.name = name;
        this.birthDt = birthDt;
        this.balance = balance;
        this.id = id;
        this.gender = gender;
    }

    public Customer() {

    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public String getBranchCode() {
        return null;
    }

    @Override
    public String getDepartmentCode() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDt() {
        return birthDt;
    }

    public void setBirthDt(Date birthDt) {
        this.birthDt = birthDt;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<String> getModeList() {
        return modeList;
    }

    public void setModeList(List<String> modeList) {
        this.modeList = modeList;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
