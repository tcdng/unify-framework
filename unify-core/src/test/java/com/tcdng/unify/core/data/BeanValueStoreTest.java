/*
 * Copyright 2018-2019 The Code Department.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

/**
 * Bean value store tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class BeanValueStoreTest {

    @Test
    public void testRetrieveSimpleBeanPropertyValue() throws Exception {
        Address address = new Address("24 Parklane", "Apapa Lagos");
        BeanValueStore bvs = new BeanValueStore(address);
        assertEquals("24 Parklane", bvs.retrieve("line1"));
        assertEquals("Apapa Lagos", bvs.retrieve("line2"));
    }

    @Test
    public void testRetrieveNestedBeanPropertyValue() throws Exception {
        Date birthDt = new Date();
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20,
                new Address("38 Warehouse Road", "Apapa Lagos"));
        BeanValueStore bvs = new BeanValueStore(customer);
        assertEquals("Amos Quito", bvs.retrieve("name"));
        assertEquals(birthDt, bvs.retrieve("birthDt"));
        assertEquals(BigDecimal.valueOf(250000.00), bvs.retrieve("balance"));
        assertEquals(Long.valueOf(20), bvs.retrieve("id"));
        assertEquals("38 Warehouse Road", bvs.retrieve("address.line1"));
        assertEquals("Apapa Lagos", bvs.retrieve("address.line2"));
    }

    @Test
    public void testRetrieveNullBeanPropertyValue() throws Exception {
        Address address = new Address();
        BeanValueStore bvs = new BeanValueStore(address);
        assertNull(bvs.retrieve("line1"));
        assertNull(bvs.retrieve("line2"));
    }

    @Test
    public void testRetrieveUnkownBeanPropertyValue() throws Exception {
        Address address = new Address("24 Parklane", "Apapa Lagos");
        BeanValueStore bvs = new BeanValueStore(address);
        assertNull(bvs.retrieve("line15"));
    }

    @Test
    public void testStoreSimpleBeanPropertyValue() throws Exception {
        Address address = new Address();
        BeanValueStore bvs = new BeanValueStore(address);
        bvs.store("line1", "37 Pauwa Road");
        bvs.store("line2", "Ungwan Dosa, Kaduna");
        assertEquals("37 Pauwa Road", address.getLine1());
        assertEquals("Ungwan Dosa, Kaduna", address.getLine2());
    }

    @Test
    public void testStoreNestedBeanPropertyValue() throws Exception {
        Date birthDt = new Date();
        Customer customer = new Customer();
        customer.setAddress(new Address());

        BeanValueStore bvs = new BeanValueStore(customer);
        bvs.store("name", "Lobsang Rampa");
        bvs.store("birthDt", birthDt);
        bvs.store("balance", BigDecimal.valueOf(20.25));
        bvs.store("id", 16);
        bvs.store("address.line1", "17, I Close, 5th Avenue");
        bvs.store("address.line2", "Festac Town");
        assertEquals("Lobsang Rampa", customer.getName());
        assertEquals(birthDt, customer.getBirthDt());
        assertEquals(BigDecimal.valueOf(20.25), customer.getBalance());
        assertEquals(Long.valueOf(16), customer.getId());
        assertEquals("17, I Close, 5th Avenue", customer.getAddress().getLine1());
        assertEquals("Festac Town", customer.getAddress().getLine2());
    }
}
