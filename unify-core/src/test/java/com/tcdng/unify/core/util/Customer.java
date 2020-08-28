/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.core.util;

import java.util.List;

/**
 * Test customer class.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Customer {

    private Long id;

    private String firstName;

    private String lastName;

    private Address address;

    private Integer[] orders;

    private List<Address> officeAddresses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer[] getOrders() {
        return orders;
    }

    public void setOrders(Integer[] orders) {
        this.orders = orders;
    }

    public List<Address> getOfficeAddresses() {
        return officeAddresses;
    }

    public void setOfficeAddresses(List<Address> officeAddresses) {
        this.officeAddresses = officeAddresses;
    }
}
