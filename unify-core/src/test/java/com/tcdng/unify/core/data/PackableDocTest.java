/*
 * Copyright 2018-2025 The Code Department.
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.Gender;

/**
 * Packable document tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class PackableDocTest extends AbstractUnifyComponentTest {

    private PackableDocConfig custDocConfig;

    private PackableDocConfig ledgerDocConfig;

    @Test
    public void testCreateSimplePackableDocument() throws Exception {
        PackableDoc pDoc = new PackableDoc(ledgerDocConfig, false);
        assertNotNull(pDoc);
    }

    @Test
    public void testCreateComplexPackableDocument() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertNotNull(pDoc);
    }

    @Test
    public void testReadSimple() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        Date birthDt = new Date();
        pDoc.write("name", "Elmer Fudd");
        pDoc.write("id", 12);
        pDoc.write("birthDt", birthDt);
        assertEquals("Elmer Fudd", pDoc.read("name"));
        assertEquals(Long.valueOf(12), pDoc.read("id"));
        assertEquals(birthDt, pDoc.read("birthDt"));
    }

    @Test
    public void testReadComplex() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        Date birthDt = new Date();
        pDoc.write("name", "Elmer Fudd");
        pDoc.write("id", 12);
        pDoc.write("birthDt", birthDt);
        pDoc.write("address", new Address("24 Parklane", "Apapa Lagos"));

        assertEquals("Elmer Fudd", pDoc.read("name"));
        assertEquals(Long.valueOf(12), pDoc.read("id"));
        assertEquals(birthDt, pDoc.read("birthDt"));

        Object val = pDoc.read(Address.class, "address");
        assertNotNull(val);
        assertEquals(Address.class, val.getClass());

        Address address = (Address) val;
        assertEquals("24 Parklane", address.getLine1());
        assertEquals("Apapa Lagos", address.getLine2());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReadWithConvertedValue() throws Exception {
        PackableDoc pDoc = new PackableDoc(ledgerDocConfig, false);
        pDoc.write("id", "15");
        pDoc.write("purchases", Arrays.asList(100.2, 15.64, 75.42));

        assertEquals(Long.valueOf(15), pDoc.read("id"));
        List<String> purchases = (List<String>) pDoc.read("purchases");
        assertEquals(3, purchases.size());
        assertEquals("100.2", purchases.get(0));
        assertEquals("15.64", purchases.get(1));
        assertEquals("75.42", purchases.get(2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleReadFrom() throws Exception {
        Ledger ledger = new Ledger("20039948858", Arrays.asList("250.50", "1823.25"), new double[] { 2.43, 5.8 });
        PackableDoc pDoc = new PackableDoc(ledgerDocConfig, false);
        assertEquals(4, pDoc.getFieldCount());

        pDoc.readFrom(ledger);
        assertNull(pDoc.read("id"));
        assertEquals("20039948858", pDoc.read("accountNo"));
        List<String> purchases = (List<String>) pDoc.read("purchases");
        assertNotNull(purchases);
        assertEquals(2, purchases.size());
        assertEquals("250.50", purchases.get(0));
        assertEquals("1823.25", purchases.get(1));

        List<Double> rates = (List<Double>) pDoc.read("rates");
        assertNotNull(rates);
        assertEquals(2, rates.size());
        assertEquals(Double.valueOf(2.43), rates.get(0));
        assertEquals(Double.valueOf(5.8), rates.get(1));
    }

    @Test
    public void testComplexReadFrom() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        Customer customer =
                new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20, address, Gender.MALE);
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertFalse(pDoc.isUpdated());
        assertEquals(7, pDoc.getFieldCount());

        pDoc.readFrom(customer);
        assertTrue(pDoc.isUpdated());
        assertEquals("Amos Quito", pDoc.read("name"));
        assertEquals(birthDt, pDoc.read("birthDt"));
        assertEquals(BigDecimal.valueOf(250000.00), pDoc.read("balance"));
        assertEquals(Long.valueOf(20), pDoc.read("id"));
        assertEquals(Gender.MALE, pDoc.read(Gender.class, "gender"));

        Object val = pDoc.read("address");
        assertNotNull(val);
        assertEquals(PackableDoc.class, val.getClass());

        PackableDoc apDoc = (PackableDoc) val;
        assertEquals("38 Warehouse Road", apDoc.read("line1"));
        assertEquals("Apapa Lagos", apDoc.read("line2"));
    }

    @Test
    public void testComplexReadFromWithBean() throws Exception {
        Date birthDt = new Date();
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20,
                new Address("38 Warehouse Road", "Apapa Lagos"), Gender.FEMALE);
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertEquals(7, pDoc.getFieldCount());

        pDoc.readFrom(customer);
        assertEquals("Amos Quito", pDoc.read("name"));
        assertEquals(birthDt, pDoc.read("birthDt"));
        assertEquals(BigDecimal.valueOf(250000.00), pDoc.read("balance"));
        assertEquals(Long.valueOf(20), pDoc.read("id"));
        Address address = (Address) pDoc.read(Address.class, "address");
        assertEquals("38 Warehouse Road", address.getLine1());
        assertEquals("Apapa Lagos", address.getLine2());
        assertEquals(Gender.FEMALE, pDoc.read(Gender.class, "gender"));
    }
    
    @Test
    public void testReadFromNoUpdate() throws Exception {
        Date birthDt = new Date();
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20L,
                Gender.FEMALE);
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);

        pDoc.readFrom(customer);
        pDoc.clearUpdated();
        
        pDoc.readFrom(customer);
        assertFalse(pDoc.isUpdated());
    }
    
    @Test
    public void testReadFromWithUpdate() throws Exception {
        Date birthDt = new Date();
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20L,
                Gender.FEMALE);
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);

        pDoc.readFrom(customer);
        pDoc.clearUpdated();
        
        customer.setGender(Gender.MALE);
        pDoc.readFrom(customer);
        assertTrue(pDoc.isUpdated());
    }


    @Test
    public void testWriteSimple() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertFalse(pDoc.isUpdated());
        pDoc.write("name", "Elmer Fudd");
        pDoc.write("id", 12);
        pDoc.write("birthDt", new Date());
        pDoc.write("address", new Address());
        assertTrue(pDoc.isUpdated());
    }

    @Test
    public void testWriteComplex() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertFalse(pDoc.isUpdated());
        pDoc.write("name", "Elmer Fudd");
        pDoc.write("id", 12);
        pDoc.write("birthDt", new Date());
        pDoc.write("address", new Address("24 Parklane", "Apapa Lagos"));
        assertTrue(pDoc.isUpdated());
    }

    @Test
    public void testWriteFieldValueWithConversion() throws Exception {
        PackableDoc pDoc = new PackableDoc(ledgerDocConfig, false);
        assertFalse(pDoc.isUpdated());
        pDoc.write("id", "15");
        pDoc.write("purchases", Arrays.asList(new double[] { 100.2, 15.64, 75.42 }));
        assertTrue(pDoc.isUpdated());
    }

    @Test
    public void testMerge() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        Date birthDt = new Date();
        pDoc.write("name", "Elmer Fudd");
        pDoc.write("id", 12);
        pDoc.write("birthDt", birthDt);
        pDoc.write("balance", BigDecimal.valueOf(106.80));
        pDoc.write("gender", Gender.MALE);
        pDoc.clearUpdated();

        PackableDoc srcDoc = new PackableDoc(custDocConfig, false);
        srcDoc.write("name", "Mary Somers");
        srcDoc.write("id", 14);
        srcDoc.write("birthDt", null);
        srcDoc.write("balance", BigDecimal.valueOf(250.23));
        srcDoc.write("gender", Gender.FEMALE);

        pDoc.merge(srcDoc, new HashSet<String>(Arrays.asList("id", "balance", "gender")));

        assertEquals("Elmer Fudd", pDoc.read("name"));
        assertEquals(Long.valueOf(14), pDoc.read("id"));
        assertEquals(birthDt, pDoc.read("birthDt"));
        assertEquals(BigDecimal.valueOf(250.23), pDoc.read("balance"));
        assertEquals(Gender.FEMALE, pDoc.read(Gender.class, "gender"));

        assertEquals("Mary Somers", srcDoc.read("name"));
        assertEquals(Long.valueOf(14), srcDoc.read("id"));
        assertNull(srcDoc.read("birthDt"));
        assertEquals(BigDecimal.valueOf(250.23), srcDoc.read("balance"));
        assertEquals(Gender.FEMALE, srcDoc.read(Gender.class, "gender"));

        assertTrue(pDoc.isUpdated());
    }

    @Test
    public void testSimpleWriteTo() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.write("name", "Hillary Clinton");
        pDoc.write("birthDt", birthDt);
        pDoc.write("balance", BigDecimal.valueOf(300));
        pDoc.write("id", 5);
        pDoc.write("address", address);

        Customer customer = new Customer();
        pDoc.writeTo(customer);
        assertEquals("Hillary Clinton", customer.getName());
        assertEquals(birthDt, customer.getBirthDt());
        assertEquals(BigDecimal.valueOf(300), customer.getBalance());
        assertEquals(Long.valueOf(5), customer.getId());
        address = (Address) pDoc.read(Address.class, "address");
        assertEquals("38 Warehouse Road", address.getLine1());
        assertEquals("Apapa Lagos", address.getLine2());
    }

    @Test(expected = UnifyException.class)
    public void testWriteUnknownField() throws Exception {
        PackableDoc pDoc = new PackableDoc(ledgerDocConfig, false);
        pDoc.write("name", "Elmer Fudd");
    }

    @Test
    public void testPackNullFieldDocument() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        byte[] packedDocument = pDoc.pack();
        assertNotNull(packedDocument);
    }

    @Test
    public void testUnpackEmptyDocument() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        byte[] packedDocument = pDoc.pack();
        PackableDoc unpackedDocument = PackableDoc.unpack(custDocConfig, packedDocument);
        assertNotNull(unpackedDocument);
    }

    @Test
    public void testSimplePackDocument() throws Exception {
        Customer customer = new Customer("Latsman", new Date(), BigDecimal.valueOf(20.0), 12,
                new Address("24 Parklane", "Apapa Lagos"), Gender.OTHER);
        customer.setModeList(Arrays.asList("A", "B", "C"));
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.readFrom(customer);
        byte[] packedDocument = pDoc.pack();
        assertNotNull(packedDocument);
    }

    @Test
    public void testSimplePackDocumentWithId() throws Exception {
        Customer customer = new Customer("Latsman", new Date(), BigDecimal.valueOf(20.0), 12,
                new Address("24 Parklane", "Apapa Lagos"), Gender.OTHER);
        customer.setModeList(Arrays.asList("A", "B", "C"));
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.setId(Long.valueOf(256L));
        pDoc.readFrom(customer);
        byte[] packedDocument = pDoc.pack();
        assertNotNull(packedDocument);
    }

    @Test
    public void testSimplePackDocumentWithReservedExtension() throws Exception {
        Customer customer = new Customer("Latsman", new Date(), BigDecimal.valueOf(20.0), 12,
                new Address("24 Parklane", "Apapa Lagos"), null);
        customer.setModeList(Arrays.asList("A", "B", "C"));
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.readFrom(customer);
        pDoc.setResrvExt(new Account("Tiwuya Hedima", BigDecimal.valueOf(200000)));
        byte[] packedDocument = pDoc.pack();
        assertNotNull(packedDocument);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleUnpackDocument() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("24 Parklane", "Apapa Lagos");
        Customer customer = new Customer("Latsman", birthDt, BigDecimal.valueOf(20.0), 12, address, Gender.OTHER);
        List<String> modeList = Arrays.asList("A", "B", "C");
        customer.setModeList(modeList);

        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.readFrom(customer);
        byte[] packedDocument = pDoc.pack();
        PackableDoc unpackedDocument = PackableDoc.unpack(custDocConfig, packedDocument, false);
        assertNotNull(unpackedDocument);
        assertEquals(7, unpackedDocument.getFieldCount());
        assertEquals("Latsman", (String) unpackedDocument.read("name"));
        assertEquals(BigDecimal.valueOf(20.0), (BigDecimal) unpackedDocument.read("balance"));
        assertEquals(birthDt, (Date) unpackedDocument.read("birthDt"));
        Address addressUnpacked = (Address) unpackedDocument.read(Address.class, "address");
        assertEquals("24 Parklane", addressUnpacked.getLine1());
        assertEquals("Apapa Lagos", addressUnpacked.getLine2());
        List<String> modeListUnpacked = (List<String>) unpackedDocument.read("modeList");
        assertEquals(modeList, modeListUnpacked);
        assertEquals(Gender.OTHER, unpackedDocument.read(Gender.class, "gender"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleUnpackDocumentWithId() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("24 Parklane", "Apapa Lagos");
        Customer customer = new Customer("Latsman", birthDt, BigDecimal.valueOf(20.0), 12, address, Gender.FEMALE);
        List<String> modeList = Arrays.asList("A", "B", "C");
        customer.setModeList(modeList);

        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.setId(Long.valueOf(1024L));
        pDoc.readFrom(customer);
        byte[] packedDocument = pDoc.pack();
        PackableDoc unpackedDocument = PackableDoc.unpack(custDocConfig, packedDocument, false);
        assertNotNull(unpackedDocument);
        assertEquals(Long.valueOf(1024L), pDoc.getId());
        assertEquals(7, unpackedDocument.getFieldCount());
        assertEquals("Latsman", (String) unpackedDocument.read("name"));
        assertEquals(BigDecimal.valueOf(20.0), (BigDecimal) unpackedDocument.read("balance"));
        assertEquals(birthDt, (Date) unpackedDocument.read("birthDt"));
        Address addressUnpacked = (Address) unpackedDocument.read(Address.class, "address");
        assertEquals("24 Parklane", addressUnpacked.getLine1());
        assertEquals("Apapa Lagos", addressUnpacked.getLine2());
        List<String> modeListUnpacked = (List<String>) unpackedDocument.read("modeList");
        assertEquals(modeList, modeListUnpacked);
        assertEquals(Gender.FEMALE, unpackedDocument.read(Gender.class, "gender"));
    }

    @Test
    public void testSimpleUnpackDocumentWithReservedExtension() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("24 Parklane", "Apapa Lagos");
        Customer customer = new Customer("Latsman", birthDt, BigDecimal.valueOf(20.0), 12, address, null);
        List<String> modeList = Arrays.asList("A", "B", "C");
        customer.setModeList(modeList);

        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.readFrom(customer);
        pDoc.setResrvExt(new Account("Aromnde Abatuba", BigDecimal.valueOf(200000)));
        byte[] packedDocument = pDoc.pack();
        PackableDoc unpackedDocument = PackableDoc.unpack(custDocConfig, packedDocument, false);
        assertNotNull(unpackedDocument);
        assertNull(unpackedDocument.getResrvExt()); // Reserved extension should be transient
    }

    @Test
    public void testExtractAuditNew() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        PackableDoc pDoc = new PackableDoc(custDocConfig, true);
        pDoc.write("name", "Hillary Clinton");
        pDoc.write("birthDt", birthDt);
        pDoc.write("balance", BigDecimal.valueOf(300));
        pDoc.write("id", 5);
        pDoc.write("address", address);

        PackableDocAudit pda = pDoc.audit();
        assertNotNull(pda);

        Map<String, Audit.TrailItem> items = pda.getTrailItems();
        assertNotNull(items);
        assertEquals(5, items.size());

        assertEquals("name", items.get("name").getFieldName());
        assertEquals("Hillary Clinton", items.get("name").getNewValue());
        assertNull(items.get("name").getOldValue());

        assertEquals("birthDt", items.get("birthDt").getFieldName());
        assertEquals(birthDt, items.get("birthDt").getNewValue());
        assertNull(items.get("birthDt").getOldValue());

        assertEquals("balance", items.get("balance").getFieldName());
        assertEquals(BigDecimal.valueOf(300), items.get("balance").getNewValue());
        assertNull(items.get("balance").getOldValue());

        assertEquals("id", items.get("id").getFieldName());
        assertEquals(Long.valueOf(5), items.get("id").getNewValue());
        assertNull(items.get("id").getOldValue());
    }

    @Test
    public void testExtractAuditUpdate() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        PackableDoc pDoc1 = new PackableDoc(custDocConfig, false);
        pDoc1.write("name", "Hillary Clinton");
        pDoc1.write("birthDt", birthDt);
        pDoc1.write("balance", BigDecimal.valueOf(300));
        pDoc1.write("id", 5);
        pDoc1.write("address", address);

        PackableDoc pDoc2 = PackableDoc.unpack(custDocConfig, pDoc1.pack(), true);
        pDoc2.write("name", "Tom Jones");
        pDoc2.write("id", 20);

        PackableDocAudit pda = pDoc2.audit();
        assertNotNull(pda);

        Map<String, Audit.TrailItem> items = pda.getTrailItems();
        assertNotNull(items);
        assertEquals(2, items.size());

        assertEquals("name", items.get("name").getFieldName());
        assertEquals("Tom Jones", items.get("name").getNewValue());
        assertEquals("Hillary Clinton", items.get("name").getOldValue());

        assertEquals("id", items.get("id").getFieldName());
        assertEquals(Long.valueOf(20), items.get("id").getNewValue());
        assertEquals(Long.valueOf(5), items.get("id").getOldValue());
    }

    @Override
    protected void onSetup() throws Exception {
        custDocConfig = PackableDocConfig.buildFrom("customerConfig", Customer.class);
        ledgerDocConfig = PackableDocConfig.buildFrom("ledgerConfig", Ledger.class);
    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
