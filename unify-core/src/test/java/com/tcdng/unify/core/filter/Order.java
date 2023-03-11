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

package com.tcdng.unify.core.filter;

/**
 * Test order class;
 * 
 * @author The Code Department
 * @since 1.0
 */
public class Order {

    private String customerName;

    private String address;

    private int quantity;

    public Order(String customerName, String address, int quantity) {
        this.customerName = customerName;
        this.address = address;
        this.quantity = quantity;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAddress() {
        return address;
    }

    public int getQuantity() {
        return quantity;
    }

}
