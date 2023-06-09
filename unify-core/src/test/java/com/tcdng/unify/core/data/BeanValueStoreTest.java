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
package com.tcdng.unify.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.Audit.TrailItem;

/**
 * Bean value store tests.
 * 
 * @author The Code Department
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
	public void testRetrieveBeanListPropertyValue() throws Exception {
		List<Address> addressList = Arrays.asList(
				new Address("24 Parklane", "Apapa Lagos"),
				new Address("38 Warehouse Road", "Apapa Lagos"));
		ValueStore bvs = new BeanValueListStore(addressList);
		bvs.setDataIndex(0);
		assertEquals("24 Parklane", bvs.retrieve("line1"));
		assertEquals("Apapa Lagos", bvs.retrieve("line2"));

		bvs.setDataIndex(1);
		assertEquals("38 Warehouse Road", bvs.retrieve("line1"));
		assertEquals("Apapa Lagos", bvs.retrieve("line2"));
	}

    @Test
	public void testRetrieveBeanListPropertyValueIteration() throws Exception {
		List<Address> addressList = Arrays.asList(
				new Address("24 Parklane", "Apapa Lagos"),
				new Address("38 Warehouse Road", "Apapa Lagos"));
		ValueStore bvs = new BeanValueListStore(addressList);
		assertTrue(bvs.next());
		assertEquals("24 Parklane", bvs.retrieve("line1"));
		assertEquals("Apapa Lagos", bvs.retrieve("line2"));

		assertTrue(bvs.next());
		assertEquals("38 Warehouse Road", bvs.retrieve("line1"));
		assertEquals("Apapa Lagos", bvs.retrieve("line2"));

		assertFalse(bvs.next());
	}

    @Test
	public void testRetrieveBeanListPropertyValueIterationReset() throws Exception {
		List<Address> addressList = Arrays.asList(
				new Address("24 Parklane", "Apapa Lagos"),
				new Address("38 Warehouse Road", "Apapa Lagos"));
		ValueStore bvs = new BeanValueListStore(addressList);
		assertTrue(bvs.next());
		assertEquals("24 Parklane", bvs.retrieve("line1"));
		assertEquals("Apapa Lagos", bvs.retrieve("line2"));

		assertTrue(bvs.next());
		assertEquals("38 Warehouse Road", bvs.retrieve("line1"));
		assertEquals("Apapa Lagos", bvs.retrieve("line2"));

		assertFalse(bvs.next());
		
		bvs.reset();
		assertTrue(bvs.next());
		assertEquals("24 Parklane", bvs.retrieve("line1"));
		assertEquals("Apapa Lagos", bvs.retrieve("line2"));

		assertTrue(bvs.next());
		assertEquals("38 Warehouse Road", bvs.retrieve("line1"));
		assertEquals("Apapa Lagos", bvs.retrieve("line2"));

		assertFalse(bvs.next());
	}

    @Test
    public void testRetrieveNestedBeanPropertyValue() throws Exception {
        Date birthDt = new Date();
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20,
                new Address("38 Warehouse Road", "Apapa Lagos"), null);
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
    public void testIsNull() throws Exception {
        Address address = new Address();
        BeanValueStore bvs = new BeanValueStore(address);
        assertTrue(bvs.isNull("line1"));
        assertTrue(bvs.isNull("line2"));
    }

    @Test
    public void testIsNotNull() throws Exception {
        Address address = new Address("24 Parklane", "Apapa Lagos");
        BeanValueStore bvs = new BeanValueStore(address);
        assertTrue(bvs.isNotNull("line1"));
        assertTrue(bvs.isNotNull("line2"));
    }

    @Test
    public void testRetrieveUnkownBeanPropertyValue() throws Exception {
        Address address = new Address("24 Parklane", "Apapa Lagos");
        BeanValueStore bvs = new BeanValueStore(address);
        assertNull(bvs.retrieve("line15"));
    }

    @Test
    public void testStoreOnNullSimpleBeanPropertyValue() throws Exception {
        Address address = new Address();
        BeanValueStore bvs = new BeanValueStore(address);
        bvs.storeOnNull("line1", "37 Pauwa Road");
        bvs.storeOnNull("line2", "Ungwan Dosa, Kaduna");
        assertEquals("37 Pauwa Road", address.getLine1());
        assertEquals("Ungwan Dosa, Kaduna", address.getLine2());

        bvs.storeOnNull("line1", "38 Pauwa Road");
        bvs.storeOnNull("line2", "Ungwan Dosa, Kastina");
        assertEquals("37 Pauwa Road", address.getLine1());
        assertEquals("Ungwan Dosa, Kaduna", address.getLine2());

        bvs.store("line1", "38 Pauwa Road");
        bvs.store("line2", "Ungwan Dosa, Kastina");
        assertEquals("38 Pauwa Road", address.getLine1());
        assertEquals("Ungwan Dosa, Kastina", address.getLine2());
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

    @Test
    public void testSaveAndRestore() throws Exception {
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
        bvs.save(Arrays.asList("name", "balance"));
        bvs.save("address.line1");
        
        bvs.store("name", "Brain Jotter");
        bvs.store("balance", BigDecimal.valueOf(250.48));
        bvs.store("address.line1", "52, J Close, 8th Avenue");
        assertEquals("Brain Jotter", customer.getName());
        assertEquals(birthDt, customer.getBirthDt());
        assertEquals(BigDecimal.valueOf(250.48), customer.getBalance());
        assertEquals(Long.valueOf(16), customer.getId());
        assertEquals("52, J Close, 8th Avenue", customer.getAddress().getLine1());
        assertEquals("Festac Town", customer.getAddress().getLine2());
        
        bvs.restore();
        assertEquals("Lobsang Rampa", customer.getName());
        assertEquals(birthDt, customer.getBirthDt());
        assertEquals(BigDecimal.valueOf(20.25), customer.getBalance());
        assertEquals(Long.valueOf(16), customer.getId());
        assertEquals("17, I Close, 5th Avenue", customer.getAddress().getLine1());
        assertEquals("Festac Town", customer.getAddress().getLine2());
    }

    @Test
    public void testIsTempValueBlank() throws Exception {
        Customer customer = new Customer();
        customer.setAddress(new Address());

        BeanValueStore bvs = new BeanValueStore(customer);
        assertFalse(bvs.isTempValue("surname"));
    }

    @Test
    public void testSetTempValue() throws Exception {
        Customer customer = new Customer();
        customer.setAddress(new Address());

        BeanValueStore bvs = new BeanValueStore(customer);
        bvs.setTempValue("surname", "Shinzo");
        assertTrue(bvs.isTempValue("surname"));
        assertFalse(bvs.isTempValue("phoneNumber"));
    }

    @Test
    public void testGetTempValue() throws Exception {
        Customer customer = new Customer();
        customer.setAddress(new Address());

        BeanValueStore bvs = new BeanValueStore(customer);
        bvs.setTempValue("surname", "Shinzo");
        assertTrue(bvs.isTempValue("surname"));
        assertFalse(bvs.isTempValue("phoneNumber"));
        assertEquals("Shinzo", bvs.getTempValue("surname"));
        assertNull(bvs.getTempValue("phoneNumber"));
    }

    @Test
    public void testGetReader() throws Exception {
        Address address = new Address("24 Parklane", "Apapa Lagos");
        BeanValueStore bvs = new BeanValueStore(address);
        ValueStoreReader reader = bvs.getReader();
        assertNotNull(reader);
        assertEquals("24 Parklane", reader.read("line1"));
        assertEquals("Apapa Lagos", reader.read("line2"));
    }

    @Test
    public void testGetWriter() throws Exception {
        BeanValueStore bvs = new BeanValueStore(new Address());
        ValueStoreWriter writer = bvs.getWriter();
        assertNotNull(writer);
        writer.write("line1", "38 Warehouse Road");
        writer.write("line2", "Apapa Lagos");

        ValueStoreReader reader = bvs.getReader();
        assertNotNull(reader);
        assertEquals("38 Warehouse Road", reader.read("line1"));
        assertEquals("Apapa Lagos", reader.read("line2"));
    }

    @Test
	public void testCompare() throws Exception {
		BeanValueStore oldValueStore = new BeanValueStore(new Address("24 Parklane", "Apapa Lagos"));
		BeanValueStore newValueStore = new BeanValueStore(new Address("24 Parklane", "Apapa Lagos"));
		int result = oldValueStore.compare(newValueStore);
		assertEquals(0, result);

		newValueStore = new BeanValueStore(new Address("38 Wharehouse Road", "Apapa Lagos"));
		result = oldValueStore.compare(newValueStore);
		assertNotEquals(0, result);
	}

    @Test
	public void testCompareWithInclusion() throws Exception {
		BeanValueStore oldValueStore = new BeanValueStore(new Address("24 Parklane", "Apapa Lagos"));
		BeanValueStore newValueStore = new BeanValueStore(new Address("24 Parklane", "Apapa Lagos"));
		int result = oldValueStore.compare(newValueStore, "line1", "line2");
		assertEquals(0, result);

		newValueStore = new BeanValueStore(new Address("38 Wharehouse Road", "Apapa Lagos"));
		result = oldValueStore.compare(newValueStore, "line1", "line2");
		assertNotEquals(0, result);
		
		result = oldValueStore.compare(newValueStore, "line2");
		assertEquals(0, result);
	}

    @Test
    public void testDiff() throws Exception {
        BeanValueStore oldValueStore = new BeanValueStore(new Address("24 Parklane", "Apapa Lagos"));
        BeanValueStore newValueStore = new BeanValueStore(new Address("24 Parklane", "Apapa Lagos"));
        Audit audit = oldValueStore.diff(newValueStore);
        assertNotNull(audit);
        assertEquals(0, audit.size());

        newValueStore = new BeanValueStore(new Address("38 Wharehouse Road", "Apapa Lagos"));
        audit = oldValueStore.diff(newValueStore);
        assertNotNull(audit);
        assertEquals(1, audit.size());
        TrailItem trial = audit.getTrailItem("line1");
        assertNotNull(trial);
        assertEquals("line1", trial.getFieldName());
        assertEquals("24 Parklane", trial.getOldValue());
        assertEquals("38 Wharehouse Road", trial.getNewValue());
    }

    @Test
    public void testCopy() throws Exception {
        Address address = new Address();
        BeanValueStore bvs = new BeanValueStore(address);
        bvs.copy(new BeanValueStore(new Address("24 Parklane", "Apapa Lagos")));
        assertEquals("24 Parklane", address.getLine1());
        assertEquals("Apapa Lagos", address.getLine2());
    }

    @Test
    public void testCopyWithExclusions() throws Exception {
        Address address = new Address();
        address.setLine2("Ibadan, Oyo");
        BeanValueStore bvs = new BeanValueStore(address);
        bvs.copyWithExclusions(new BeanValueStore(new Address("24 Parklane", "Apapa Lagos")), "line2");
        assertEquals("24 Parklane", address.getLine1());
        assertEquals("Ibadan, Oyo", address.getLine2());
    }

    @Test
    public void testCopyWithInclusions() throws Exception {
        Address address = new Address();
        address.setLine2("Ibadan, Oyo");
        BeanValueStore bvs = new BeanValueStore(address);
        bvs.copyWithInclusions(new BeanValueStore(new Address("24 Parklane", "Apapa Lagos")), "line2");
        assertNull(address.getLine1());
        assertEquals("Apapa Lagos", address.getLine2());
    }

    @Test
    public void testPolicyRetrieveSimpleBeanPropertyValue() throws Exception {
        Address address = new Address("24 Parklane", "Apapa Lagos");
        BeanValueStore bvs = new BeanValueStore(address);
        bvs.setPolicy(new TestValueStorePolicy());
        assertEquals("Apapa Lagos", bvs.retrieve("line1"));
        assertEquals("Apapa Lagos", bvs.retrieve("line2"));
        
        address = new Address("38 Warehouse Road", "Apapa Lagos");
        bvs = new BeanValueStore(address);
        bvs.setPolicy(new TestValueStorePolicy());
        assertEquals("38 Warehouse Road", bvs.retrieve("line1"));
        assertEquals("Apapa Lagos", bvs.retrieve("line2"));
    }

    @Test
    public void testPolicyStoreSimpleBeanPropertyValue() throws Exception {
        Address address = new Address();
        BeanValueStore bvs = new BeanValueStore(address);
        bvs.setPolicy(new TestValueStorePolicy());
        bvs.store("line1", "37 Pauwa Road");
        bvs.store("line2", "Ungwan Dosa, Kaduna");
        assertEquals("surf.37 Pauwa Road", address.getLine1());
        assertEquals("Ungwan Dosa, Kaduna", address.getLine2());
    }

    @Test
    public void testTempValueRetrievalPreference() throws Exception {
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
        assertEquals(BigDecimal.valueOf(20.25), bvs.retrieve("balance"));
        
        bvs.setTempValue("balance", BigDecimal.valueOf(302.67));
        assertEquals(BigDecimal.valueOf(302.67), bvs.retrieve("balance"));
 
        bvs.setTempValue("balance", null);
        assertEquals(BigDecimal.valueOf(20.25), bvs.retrieve("balance"));
    }
  
    private class TestValueStorePolicy implements ValueStorePolicy {

        @Override
        public Object onRetrieve(ValueStore valueStore, String name, Object val) throws UnifyException {
            if ("line1".equals(name)) {
                if (val != null && ((String) val).contains("Parklane")) {
                    val = valueStore.retrieve("line2");
                }
            }
            
            return val;
        }

        @Override
        public Object onStore(ValueStore valueStore, String name, Object val) throws UnifyException {
            if ("line1".equals(name)) {
                val = "surf." + val;
            }

            return val;
        }
        
    }
}
