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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.UnifyException;

/**
 * PackableDoc value store tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class PackableDocValueStoreTest extends AbstractUnifyComponentTest {

    private PackableDocConfig custDocConfig;

    private PackableDocConfig addressDocConfig;

    @Test
    public void testRetrieveDocumentFieldValue() throws Exception {
        PackableDoc pd = new PackableDoc(custDocConfig, false);
        Date birthDt = new Date();
        pd.write("name", "Amos Quito");
        pd.write("birthDt", birthDt);
        pd.write("balance", BigDecimal.valueOf(250000.00));
        pd.write("id", 16);
        pd.write("address", new Address());
        pd.write("address.line1", "Whipper!");

        PackableDocStore dvs = new PackableDocStore(pd);
        assertEquals("Amos Quito", dvs.retrieve("name"));
        assertEquals(birthDt, dvs.retrieve("birthDt"));
        assertEquals(BigDecimal.valueOf(250000.00), dvs.retrieve("balance"));
        assertEquals(Long.valueOf(16), dvs.retrieve("id"));
        assertEquals("Whipper!", dvs.retrieve("address.line1"));
    }

    @Test
    public void testRetrieveNullDocumentFieldValue() throws Exception {
        PackableDoc pd = new PackableDoc(addressDocConfig, false);
        PackableDocStore dvs = new PackableDocStore(pd);
        assertNull(dvs.retrieve("line1"));
        assertNull(dvs.retrieve("line2"));
    }

    @Test(expected = UnifyException.class)
    public void testRetrieveUnkownDocumentFieldValue() throws Exception {
        PackableDocStore dvs = new PackableDocStore(new PackableDoc(addressDocConfig, false));
        assertNull(dvs.retrieve("line15"));
    }

    @Test(expected = NullPointerException.class)
    public void testRetrieveFromNullDocument() throws Exception {
        PackableDocStore dvs = new PackableDocStore(null);
        assertNull(dvs.retrieve("line15"));
    }

    @Test
    public void testStoreDocumentFieldValue() throws Exception {
        PackableDoc pd = new PackableDoc(addressDocConfig, false);
        PackableDocStore dvs = new PackableDocStore(pd);
        dvs.store("line1", "37 Pauwa Road");
        dvs.store("line2", "Ungwan Dosa, Kaduna");
        assertEquals("37 Pauwa Road", pd.read("line1"));
        assertEquals("Ungwan Dosa, Kaduna", pd.read("line2"));
    }

    @Test(expected = NullPointerException.class)
    public void testStoreNullDocument() throws Exception {
        PackableDocStore dvs = new PackableDocStore(null);
        dvs.store("line12", "Rabbids");
    }

    @Test
    public void testRetrieveReservedExtensionNull() throws Exception {
        PackableDocStore dvs = new PackableDocStore(new PackableDoc(custDocConfig, false));
        assertNull(dvs.retrieve(PackableDoc.RESERVED_EXT_FIELD));
    }

    @Test
    public void testRetrieveReservedExtension() throws Exception {
        PackableDoc pd = new PackableDoc(custDocConfig, false);
        pd.setResrvExt(new Account("Amos", BigDecimal.valueOf(25.45)));

        PackableDocStore dvs = new PackableDocStore(pd);
        Account acct = (Account) dvs.retrieve(PackableDoc.RESERVED_EXT_FIELD);
        assertNotNull(acct);
        assertEquals("Amos", acct.getAccountNo());
        assertEquals(BigDecimal.valueOf(25.45), acct.getBalance());
    }

    @Test
    public void testRetrieveReservedExtensionNested() throws Exception {
        PackableDoc pd = new PackableDoc(custDocConfig, false);
        pd.setResrvExt(new Account("Amos", BigDecimal.valueOf(25.45)));

        PackableDocStore dvs = new PackableDocStore(pd);
        assertEquals("Amos", dvs.retrieve(PackableDoc.RESERVED_EXT_FIELD + ".accountNo"));
        assertEquals(BigDecimal.valueOf(25.45), dvs.retrieve(PackableDoc.RESERVED_EXT_FIELD + ".balance"));
    }

    @Test
    public void testStoreReservedExtension() throws Exception {
        PackableDoc pd = new PackableDoc(custDocConfig, false);

        PackableDocStore dvs = new PackableDocStore(pd);
        dvs.store(PackableDoc.RESERVED_EXT_FIELD, new Account("Ben", BigDecimal.TEN));
        Account acct = (Account) pd.getResrvExt();
        assertNotNull(acct);
        assertEquals("Ben", acct.getAccountNo());
        assertEquals(BigDecimal.TEN, acct.getBalance());
    }

    @Test
    public void testStoreReservedExtensionNested() throws Exception {
        PackableDoc pd = new PackableDoc(custDocConfig, false);
        pd.setResrvExt(new Account());

        PackableDocStore dvs = new PackableDocStore(pd);
        dvs.store(PackableDoc.RESERVED_EXT_FIELD + ".accountNo", "Bruce Banner");
        dvs.store(PackableDoc.RESERVED_EXT_FIELD + ".balance", BigDecimal.ONE);
        Account acct = (Account) pd.getResrvExt();
        assertNotNull(acct);
        assertEquals("Bruce Banner", acct.getAccountNo());
        assertEquals(BigDecimal.ONE, acct.getBalance());
    }

    @Test
    public void testReservedExtensionGettableNull() throws Exception {
        PackableDocStore dvs = new PackableDocStore(new PackableDoc(custDocConfig, false));
        assertTrue(dvs.isGettable(PackableDoc.RESERVED_EXT_FIELD));
    }

    @Test
    public void testReservedExtensionGettable() throws Exception {
        PackableDoc pd = new PackableDoc(custDocConfig, false);
        pd.setResrvExt(new Account());
        PackableDocStore dvs = new PackableDocStore(pd);
        assertTrue(dvs.isGettable(PackableDoc.RESERVED_EXT_FIELD + ".accountNo"));
        assertTrue(dvs.isGettable(PackableDoc.RESERVED_EXT_FIELD + ".balance"));
        assertFalse(dvs.isGettable(PackableDoc.RESERVED_EXT_FIELD + ".details"));
    }

    @Test
    public void testReservedExtensionSettableNull() throws Exception {
        PackableDocStore dvs = new PackableDocStore(new PackableDoc(custDocConfig, false));
        assertTrue(dvs.isSettable(PackableDoc.RESERVED_EXT_FIELD));
    }

    @Test
    public void testReservedExtensionSettable() throws Exception {
        PackableDoc pd = new PackableDoc(custDocConfig, false);
        pd.setResrvExt(new Account());
        PackableDocStore dvs = new PackableDocStore(pd);
        assertTrue(dvs.isSettable(PackableDoc.RESERVED_EXT_FIELD + ".accountNo"));
        assertTrue(dvs.isSettable(PackableDoc.RESERVED_EXT_FIELD + ".balance"));
        assertFalse(dvs.isSettable(PackableDoc.RESERVED_EXT_FIELD + ".details"));
    }

    @Override
    protected void onSetup() throws Exception {
        custDocConfig = PackableDocConfig.buildFrom("customerConfig", Customer.class);
        addressDocConfig = PackableDocConfig.buildFrom("addressConfig", Address.class);
    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
