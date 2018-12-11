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
package com.tcdng.unify.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * ReflectUtils tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ReflectUtilsTest {

	@Test
	public void testIsGettableField() throws Exception {
		assertTrue(ReflectUtils.isGettableField(Customer.class, "firstName"));
		assertTrue(ReflectUtils.isGettableField(Customer.class, "address.addressLine1"));
	}

	@Test
	public void testIsSettableField() throws Exception {
		assertTrue(ReflectUtils.isSettableField(Customer.class, "firstName"));
		assertTrue(ReflectUtils.isSettableField(Customer.class, "address.addressLine1"));
	}

	@Test
	public void testHashCode() throws Exception {
		ContactAddress contactAddressA = new ContactAddress();
		contactAddressA.setAddressLine1("Main");
		contactAddressA.setAddressLine2("Carey");
		ContactAddress contactAddressB = new ContactAddress();
		contactAddressB.setAddressLine1("Main");
		contactAddressB.setAddressLine2("Carey");
		ContactAddress contactAddressC = new ContactAddress();
		contactAddressC.setAddressLine1(null);
		contactAddressC.setAddressLine2("Carey");
		ContactAddress contactAddressD = new ContactAddress();
		contactAddressD.setAddressLine1(null);
		contactAddressD.setAddressLine2("Carey");
		assertEquals(ReflectUtils.hashCode(contactAddressA), ReflectUtils.hashCode(contactAddressB));
		assertNotSame(ReflectUtils.hashCode(contactAddressA), ReflectUtils.hashCode(contactAddressC));
		assertNotSame(ReflectUtils.hashCode(contactAddressB), ReflectUtils.hashCode(contactAddressC));
		assertEquals(ReflectUtils.hashCode(contactAddressD), ReflectUtils.hashCode(contactAddressC));
	}

	@Test
	public void testEquals() throws Exception {
		Address addressA = new Address();
		addressA.setAddressLine1("Main");
		addressA.setAddressLine2("Carey");
		Address addressB = new Address();
		addressB.setAddressLine1("Main");
		addressB.setAddressLine2("Carey");
		Address addressC = new Address();
		addressC.setAddressLine1(null);
		addressC.setAddressLine2("Carey");
		Address addressD = new Address();
		addressD.setAddressLine1(null);
		addressD.setAddressLine2("Carey");
		assertTrue(ReflectUtils.equals(addressA, addressB));
		assertTrue(ReflectUtils.equals(addressC, addressD));
	}

	@Test
	public void testNotEquals() throws Exception {
		Address addressA = new Address();
		addressA.setAddressLine1("Main");
		addressA.setAddressLine2("Carey");
		Address addressB = new ContactAddress();
		addressB.setAddressLine1(null);
		addressB.setAddressLine2("Carey");
		assertFalse(ReflectUtils.equals(addressA, addressB));
	}

	@Test
	public void testEqualsWithIgnore() throws Exception {
		ContactAddress contactAddressA = new ContactAddress();
		contactAddressA.setAddressLine1("Main");
		contactAddressA.setAddressLine2("Carey");
		contactAddressA.setVersionNo(10);
		ContactAddress contactAddressB = new ContactAddress();
		contactAddressB.setAddressLine1("Main");
		contactAddressB.setAddressLine2("Carey");
		contactAddressA.setVersionNo(20);
		assertTrue(ReflectUtils.equals(contactAddressA, contactAddressB, "versionNo"));
	}

	@Test
	public void testNotEqualsWithIgnore() throws Exception {
		ContactAddress contactAddressA = new ContactAddress();
		contactAddressA.setAddressLine1("Main");
		contactAddressA.setAddressLine2("Carey");
		contactAddressA.setVersionNo(10);
		ContactAddress contactAddressB = new ContactAddress();
		contactAddressB.setAddressLine1("Main");
		contactAddressB.setAddressLine2("Tom");
		contactAddressA.setVersionNo(20);
		assertFalse(ReflectUtils.equals(contactAddressA, contactAddressB, "versionNo"));
	}

	@Test
	public void testGetBeanCompliantFieldNames() throws Exception {
		List<String> fieldNames1 = ReflectUtils.getBeanCompliantFieldNames(Customer.class);
		assertNotNull(fieldNames1);
		assertEquals(6, fieldNames1.size());
		assertTrue(fieldNames1.contains("address"));
		assertTrue(fieldNames1.contains("firstName"));
		assertTrue(fieldNames1.contains("id"));
		assertTrue(fieldNames1.contains("lastName"));
		assertTrue(fieldNames1.contains("officeAddresses"));
		assertTrue(fieldNames1.contains("orders"));

		List<String> fieldNames2 = ReflectUtils.getBeanCompliantFieldNames(Customer.class.getName());
		assertSame(fieldNames1, fieldNames2);
		assertEquals(fieldNames1, fieldNames2);
	}

	@Test
	public void testGetBeanCompliantNestedFieldNames() throws Exception {
		List<String> fieldNames1 = ReflectUtils.getBeanCompliantNestedFieldNames(Customer.class);
		assertNotNull(fieldNames1);
		assertEquals(8, fieldNames1.size());
		assertTrue(fieldNames1.contains("address"));
		assertTrue(fieldNames1.contains("address.addressLine1"));
		assertTrue(fieldNames1.contains("address.addressLine2"));
		assertTrue(fieldNames1.contains("firstName"));
		assertTrue(fieldNames1.contains("id"));
		assertTrue(fieldNames1.contains("lastName"));
		assertTrue(fieldNames1.contains("officeAddresses"));
		assertTrue(fieldNames1.contains("orders"));

		List<String> fieldNames2 = ReflectUtils.getBeanCompliantNestedFieldNames(Customer.class.getName());
		assertSame(fieldNames1, fieldNames2);
		assertEquals(fieldNames1, fieldNames2);
	}

	@Test
	public void testGetNestedFieldType() throws Exception {
		assertEquals(Long.class, ReflectUtils.getNestedFieldType(Customer.class, "id"));
		assertEquals(Address.class, ReflectUtils.getNestedFieldType(Customer.class, "address"));
		assertEquals(Integer[].class, ReflectUtils.getNestedFieldType(Customer.class, "orders"));
		assertEquals(String.class, ReflectUtils.getNestedFieldType(Customer.class, "firstName"));
		assertEquals(String.class, ReflectUtils.getNestedFieldType(Customer.class, "lastName"));
		assertEquals(String.class, ReflectUtils.getNestedFieldType(Customer.class, "address.addressLine1"));
		assertEquals(String.class, ReflectUtils.getNestedFieldType(Customer.class, "address.addressLine2"));
	}

	@Test
	public void testWrappedDataIsGettableField() throws Exception {
		assertTrue(ReflectUtils.isGettableField(WrappedAddress.class, "data.addressLine1"));
		assertTrue(ReflectUtils.isGettableField(WrappedAddress.class, "data.addressLine2"));
		assertTrue(ReflectUtils.isGettableField(WrappedAddress.class, "addressLine3"));
	}

	@Test
	public void testWrappedDataIsSettableField() throws Exception {
		assertTrue(ReflectUtils.isSettableField(WrappedAddress.class, "data.addressLine1"));
		assertTrue(ReflectUtils.isSettableField(WrappedAddress.class, "data.addressLine2"));
		assertTrue(ReflectUtils.isSettableField(WrappedAddress.class, "addressLine3"));
	}

	@Test
	public void testGetWrappedDataCompliantNestedFieldNames() throws Exception {
		List<String> fieldNames = ReflectUtils.getBeanCompliantNestedFieldNames(WrappedAddress.class);
		assertNotNull(fieldNames);
		assertEquals(3, fieldNames.size());
		assertTrue(fieldNames.contains("data.addressLine1"));
		assertTrue(fieldNames.contains("data.addressLine2"));
		assertTrue(fieldNames.contains("addressLine3"));
	}
}
