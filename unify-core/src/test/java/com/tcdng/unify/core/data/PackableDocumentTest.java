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
package com.tcdng.unify.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.PackableDocConfig.FieldConfig;
import com.tcdng.unify.core.data.PackableDocRWConfig.FieldMapping;
import com.tcdng.unify.core.util.StringUtils;

/**
 * PackableDoc tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PackableDocumentTest extends AbstractUnifyComponentTest {

    private PackableDocConfig custDocConfig;

    private PackableDocRWConfig custDocRwConfig;

    private PackableDocConfig xCustDocConfig;

    private PackableDocRWConfig xCustDocRwConfig;

    private PackableDocConfig ledgerDocConfig;

    @Test
    public void testCreateSimplePackableDocument() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertNotNull(pDoc);
    }

    @Test
    public void testCreateComplexPackableDocument() throws Exception {
        PackableDoc pDoc = new PackableDoc(xCustDocConfig, false);
        assertNotNull(pDoc);
    }

    @Test
    public void testSimpleReadFrom() throws Exception {
        Date birthDt = new Date();
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20, null);
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertEquals(6, pDoc.getFieldCount());

        pDoc.readFrom(custDocRwConfig, customer);
        assertEquals("Amos Quito", pDoc.readFieldValue("name"));
        assertEquals(birthDt, pDoc.readFieldValue("birthDt"));
        assertEquals(BigDecimal.valueOf(250000.00), pDoc.readFieldValue("balance"));
        assertEquals(Long.valueOf(20), pDoc.readFieldValue("id"));
        assertNull(pDoc.readFieldValue("address"));
    }

    @Test
    public void testComplexReadFrom() throws Exception {
        Date birthDt = new Date();
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20, null);
        PackableDoc pDoc = new PackableDoc(xCustDocConfig, false);
        assertEquals(6, pDoc.getFieldCount());

        pDoc.readFrom(xCustDocRwConfig, customer);
        assertEquals("Amos Quito", pDoc.readFieldValue("name"));
        assertEquals(birthDt, pDoc.readFieldValue("birthDt"));
        assertEquals(BigDecimal.valueOf(250000.00), pDoc.readFieldValue("balance"));
        assertEquals(Long.valueOf(20), pDoc.readFieldValue("id"));
        assertNull(pDoc.readFieldValue("address"));
    }

    @Test
    public void testSimpleReadFromWithBean() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20, address);
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertEquals(6, pDoc.getFieldCount());

        pDoc.readFrom(custDocRwConfig, customer);
        assertEquals("Amos Quito", pDoc.readFieldValue("name"));
        assertEquals(birthDt, pDoc.readFieldValue("birthDt"));
        assertEquals(BigDecimal.valueOf(250000.00), pDoc.readFieldValue("balance"));
        assertEquals(Long.valueOf(20), pDoc.readFieldValue("id"));
        assertEquals(address, pDoc.readFieldValue("address"));
    }

    @Test
    public void testComplexReadFromWithBean() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20, address);
        PackableDoc pDoc = new PackableDoc(xCustDocConfig, false);
        assertEquals(6, pDoc.getFieldCount());

        pDoc.readFrom(xCustDocRwConfig, customer);
        assertEquals("Amos Quito", pDoc.readFieldValue("name"));
        assertEquals(birthDt, pDoc.readFieldValue("birthDt"));
        assertEquals(BigDecimal.valueOf(250000.00), pDoc.readFieldValue("balance"));
        assertEquals(Long.valueOf(20), pDoc.readFieldValue("id"));

        Object val = pDoc.readFieldValue("address");
        assertNotNull(val);
        assertEquals(PackableDoc.class, val.getClass());

        PackableDoc apDoc = (PackableDoc) val;
        assertEquals("38 Warehouse Road", apDoc.readFieldValue("line1"));
        assertEquals("Apapa Lagos", apDoc.readFieldValue("line2"));
    }

    @Test
    public void testSimpleReadFromWithList() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20, address);
        List<String> modeList = Arrays.asList("A", "B", "C");
        customer.setModeList(modeList);

        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertEquals(6, pDoc.getFieldCount());

        pDoc.readFrom(custDocRwConfig, customer);
        assertEquals(6, pDoc.getFieldCount());
        assertEquals("Amos Quito", pDoc.readFieldValue("name"));
        assertEquals(birthDt, pDoc.readFieldValue("birthDt"));
        assertEquals(BigDecimal.valueOf(250000.00), pDoc.readFieldValue("balance"));
        assertEquals(Long.valueOf(20), pDoc.readFieldValue("id"));
        assertEquals(address, pDoc.readFieldValue("address"));
        assertEquals(modeList, pDoc.readFieldValue("modeList"));
    }

    @Test
    public void testComplexReadFromWithList() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20, address);
        List<String> modeList = Arrays.asList("A", "B", "C");
        customer.setModeList(modeList);

        PackableDoc pDoc = new PackableDoc(xCustDocConfig, false);
        assertEquals(6, pDoc.getFieldCount());

        pDoc.readFrom(xCustDocRwConfig, customer);
        assertEquals(6, pDoc.getFieldCount());
        assertEquals("Amos Quito", pDoc.readFieldValue("name"));
        assertEquals(birthDt, pDoc.readFieldValue("birthDt"));
        assertEquals(BigDecimal.valueOf(250000.00), pDoc.readFieldValue("balance"));
        assertEquals(Long.valueOf(20), pDoc.readFieldValue("id"));

        Object val = pDoc.readFieldValue("address");
        assertNotNull(val);
        assertEquals(PackableDoc.class, val.getClass());

        PackableDoc apDoc = (PackableDoc) val;
        assertEquals("38 Warehouse Road", apDoc.readFieldValue("line1"));
        assertEquals("Apapa Lagos", apDoc.readFieldValue("line2"));
        assertEquals(modeList, pDoc.readFieldValue("modeList"));
    }

    @Test
    public void testGetDocumentFieldNames() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        Set<String> names = pDoc.getFieldNames();
        assertEquals(6, names.size());
        assertTrue(names.contains("name"));
        assertTrue(names.contains("id"));
        assertTrue(names.contains("birthDt"));
        assertTrue(names.contains("address"));
        assertTrue(names.contains("modeList"));
    }

    @Test
    public void testGetDocumentFieldType() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertEquals(String.class, pDoc.getFieldType("name"));
        assertEquals(Long.class, pDoc.getFieldType("id"));
        assertEquals(Date.class, pDoc.getFieldType("birthDt"));
        assertEquals(Address.class, pDoc.getFieldType("address"));
        assertEquals(List.class, pDoc.getFieldType("modeList"));
    }

    @Test(expected = UnifyException.class)
    public void testGetDocumentUnknownFieldType() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.getFieldType("salary");
    }

    @Test
    public void testWriteFieldValue() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.writeFieldValue("name", "Elmer Fudd");
        pDoc.writeFieldValue("id", 12);
        pDoc.writeFieldValue("birthDt", new Date());
        pDoc.writeFieldValue("address", new Address());
    }

    @Test
    public void testWriteComplexFieldValue() throws Exception {
        PackableDoc pDoc = new PackableDoc(xCustDocConfig, false);
        pDoc.writeFieldValue("name", "Elmer Fudd");
        pDoc.writeFieldValue("id", 12);
        pDoc.writeFieldValue("birthDt", new Date());
        pDoc.writeFieldValue(xCustDocRwConfig, "address", new Address("24 Parklane", "Apapa Lagos"));
    }

//    @Test
//    public void testWriteComplexFieldValueWithPreset() throws Exception {
//        PackableDoc pDoc = new PackableDoc(xCustDocConfig, false).preset();
//        pDoc.writeFieldValue("name", "Elmer Fudd");
//        pDoc.writeFieldValue("id", 12);
//        pDoc.writeFieldValue("birthDt", new Date());
//        pDoc.writeFieldValue(xCustDocRwConfig, "address.line1", "24 Parklane");
//        pDoc.writeFieldValue(xCustDocRwConfig, "address.line2", "Apapa Lagos");
//    }

    @Test
    public void testWriteFieldValueWithConversion() throws Exception {
        PackableDoc pDoc = new PackableDoc(ledgerDocConfig, false);
        pDoc.writeFieldValue("id", "15");
        pDoc.writeFieldValue("purchases", new double[] { 100.2, 15.64, 75.42 });
    }

    @Test(expected = UnifyException.class)
    public void testWriteUnknownDocumentFieldValue() throws Exception {
        PackableDoc pDoc = new PackableDoc(ledgerDocConfig, false);
        pDoc.writeFieldValue("name", "Elmer Fudd");
    }

    @Test(expected = UnifyException.class)
    public void testWriteFieldValueWithInvalidConversion() throws Exception {
        PackableDoc pDoc = new PackableDoc(ledgerDocConfig, false);
        pDoc.writeFieldValue("age", "Fifteen");
    }

    @Test
    public void testReadFieldValue() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        Date birthDt = new Date();
        pDoc.writeFieldValue("name", "Elmer Fudd");
        pDoc.writeFieldValue("id", 12);
        pDoc.writeFieldValue("birthDt", birthDt);
        assertEquals("Elmer Fudd", pDoc.readFieldValue("name"));
        assertEquals(Long.valueOf(12), pDoc.readFieldValue("id"));
        assertEquals(birthDt, pDoc.readFieldValue("birthDt"));
    }

    @Test
    public void testReadComplexFieldValue() throws Exception {
        PackableDoc pDoc = new PackableDoc(xCustDocConfig, false);
        Date birthDt = new Date();
        pDoc.writeFieldValue("name", "Elmer Fudd");
        pDoc.writeFieldValue("id", 12);
        pDoc.writeFieldValue("birthDt", birthDt);
        pDoc.writeFieldValue(xCustDocRwConfig, "address", new Address("24 Parklane", "Apapa Lagos"));

        assertEquals("Elmer Fudd", pDoc.readFieldValue("name"));
        assertEquals(Long.valueOf(12), pDoc.readFieldValue("id"));
        assertEquals(birthDt, pDoc.readFieldValue("birthDt"));

        Object val = pDoc.readFieldValue(xCustDocRwConfig, "address");
        assertNotNull(val);
        assertEquals(Address.class, val.getClass());

        Address address = (Address) val;
        assertEquals("24 Parklane", address.getLine1());
        assertEquals("Apapa Lagos", address.getLine2());
    }

    @Test
    public void testReadFieldWithConvertedValue() throws Exception {
        PackableDoc pDoc = new PackableDoc(ledgerDocConfig, false);
        pDoc.writeFieldValue("id", "15");
        pDoc.writeFieldValue("purchases", new double[] { 100.2, 15.64, 75.42 });

        assertEquals(Long.valueOf(15), pDoc.readFieldValue("id"));
        String[] purchases = (String[]) pDoc.readFieldValue("purchases");
        assertEquals(3, purchases.length);
        assertEquals("100.2", purchases[0]);
        assertEquals("15.64", purchases[1]);
        assertEquals("75.42", purchases[2]);
    }

    @Test(expected = UnifyException.class)
    public void testReadUnknownDocumentFieldValue() throws Exception {
        PackableDoc pDoc = new PackableDoc(ledgerDocConfig, false);
        pDoc.readFieldValue("name");
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
                new Address("24 Parklane", "Apapa Lagos"));
        customer.setModeList(Arrays.asList("A", "B", "C"));
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.readFrom(custDocRwConfig, customer);
        byte[] packedDocument = pDoc.pack();
        assertNotNull(packedDocument);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleUnpackDocument() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("24 Parklane", "Apapa Lagos");
        Customer customer = new Customer("Latsman", birthDt, BigDecimal.valueOf(20.0), 12, address);
        List<String> modeList = Arrays.asList("A", "B", "C");
        customer.setModeList(modeList);

        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.readFrom(custDocRwConfig, customer);
        byte[] packedDocument = pDoc.pack();
        PackableDoc unpackedDocument = PackableDoc.unpack(custDocConfig, packedDocument, false);
        assertNotNull(unpackedDocument);
        assertEquals(6, unpackedDocument.getFieldCount());
        assertEquals("Latsman", (String) unpackedDocument.readFieldValue("name"));
        assertEquals(BigDecimal.valueOf(20.0), (BigDecimal) unpackedDocument.readFieldValue("balance"));
        assertEquals(birthDt, (Date) unpackedDocument.readFieldValue("birthDt"));
        Address addressUnpacked = (Address) unpackedDocument.readFieldValue("address");
        assertEquals("24 Parklane", addressUnpacked.getLine1());
        assertEquals("Apapa Lagos", addressUnpacked.getLine2());
        List<String> modeListUnpacked = (List<String>) unpackedDocument.readFieldValue("modeList");
        assertEquals(modeList, modeListUnpacked);

        assertEquals(String.class, unpackedDocument.getFieldType("name"));
        assertEquals(BigDecimal.class, unpackedDocument.getFieldType("balance"));
        assertEquals(Long.class, unpackedDocument.getFieldType("id"));
        assertEquals(Date.class, unpackedDocument.getFieldType("birthDt"));
        assertEquals(Address.class, unpackedDocument.getFieldType("address"));
        assertEquals(List.class, unpackedDocument.getFieldType("modeList"));
    }

    @Test
    public void testSimpleWriteToBean() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        pDoc.writeFieldValue("name", "Hillary Clinton");
        pDoc.writeFieldValue("birthDt", birthDt);
        pDoc.writeFieldValue("balance", BigDecimal.valueOf(300));
        pDoc.writeFieldValue("id", 5);
        pDoc.writeFieldValue("address", address);

        Customer customer = new Customer();
        pDoc.writeTo(custDocRwConfig, customer);
        assertEquals("Hillary Clinton", customer.getName());
        assertEquals(birthDt, customer.getBirthDt());
        assertEquals(BigDecimal.valueOf(300), customer.getBalance());
        assertEquals(Long.valueOf(5), customer.getId());
        assertEquals(address, customer.getAddress());
    }

    @Test
    public void testExtractAuditNew() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        PackableDoc pDoc = new PackableDoc(custDocConfig, true);
        pDoc.writeFieldValue("name", "Hillary Clinton");
        pDoc.writeFieldValue("birthDt", birthDt);
        pDoc.writeFieldValue("balance", BigDecimal.valueOf(300));
        pDoc.writeFieldValue("id", 5);
        pDoc.writeFieldValue("address", address);

        PackableDocAudit pda = pDoc.audit();
        assertNotNull(pda);

        Map<String, PackableDocAudit.TrailItem> items = pda.getTrailItems();
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

        assertEquals("address", items.get("address").getFieldName());
        assertEquals(address, items.get("address").getNewValue());
        assertNull(items.get("address").getOldValue());
    }

    @Test
    public void testExtractAuditUpdate() throws Exception {
        Date birthDt = new Date();
        Address address = new Address("38 Warehouse Road", "Apapa Lagos");
        PackableDoc pDoc1 = new PackableDoc(custDocConfig, false);
        pDoc1.writeFieldValue("name", "Hillary Clinton");
        pDoc1.writeFieldValue("birthDt", birthDt);
        pDoc1.writeFieldValue("balance", BigDecimal.valueOf(300));
        pDoc1.writeFieldValue("id", 5);
        pDoc1.writeFieldValue("address", address);

        PackableDoc pDoc2 = PackableDoc.unpack(custDocConfig, pDoc1.pack(), true);
        pDoc2.writeFieldValue("name", "Tom Jones");
        pDoc2.writeFieldValue("id", 20);

        PackableDocAudit pda = pDoc2.audit();
        assertNotNull(pda);

        Map<String, PackableDocAudit.TrailItem> items = pda.getTrailItems();
        assertNotNull(items);
        assertEquals(2, items.size());

        assertEquals("name", items.get("name").getFieldName());
        assertEquals("Tom Jones", items.get("name").getNewValue());
        assertEquals("Hillary Clinton", items.get("name").getOldValue());

        assertEquals("id", items.get("id").getFieldName());
        assertEquals(Long.valueOf(20), items.get("id").getNewValue());
        assertEquals(Long.valueOf(5), items.get("id").getOldValue());
    }

    @Test
    public void testDescribePackableDocumentEmpty() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        assertEquals("Customer - null", pDoc.describe(StringUtils.breakdownParameterizedString("Customer - {name}")));
    }

    @Test
    public void testDescribePackableDocument() throws Exception {
        PackableDoc pDoc = new PackableDoc(custDocConfig, false);
        Date birthDt = new Date();
        Customer customer = new Customer("Amos Quito", birthDt, BigDecimal.valueOf(250000.00), 20, null);
        pDoc.readFrom(custDocRwConfig, customer);
        assertEquals("Customer - Amos Quito",
                pDoc.describe(StringUtils.breakdownParameterizedString("Customer - {name}")));
    }

    @Override
    protected void onSetup() throws Exception {
        custDocConfig = new PackableDocConfig("customerConfig", new FieldConfig("name", String.class),
                new FieldConfig("birthDt", Date.class), new FieldConfig("balance", BigDecimal.class),
                new FieldConfig("id", Long.class), new FieldConfig("address", Address.class),
                new FieldConfig("modeList", List.class));

        custDocRwConfig = new PackableDocRWConfig(Customer.class, new FieldMapping("name", "name"),
                new FieldMapping("birthDt", "birthDt"), new FieldMapping("balance", "balance"),
                new FieldMapping("id", "id"), new FieldMapping("address", "address"),
                new FieldMapping("modeList", "modeList"));

        ledgerDocConfig = new PackableDocConfig("ledgerConfig", new FieldConfig("id", Long.class),
                new FieldConfig("purchases", String[].class));

        xCustDocConfig = new PackableDocConfig("customerConfig", new FieldConfig("name", String.class),
                new FieldConfig("birthDt", Date.class), new FieldConfig("balance", BigDecimal.class),
                new FieldConfig("id", Long.class), new FieldConfig("address", PackableDoc.class,
                        new FieldConfig("line1", String.class), new FieldConfig("line2", String.class)),
                new FieldConfig("modeList", List.class));

        xCustDocRwConfig = new PackableDocRWConfig(Customer.class, new FieldMapping("name", "name"),
                new FieldMapping("birthDt", "birthDt"), new FieldMapping("balance", "balance"),
                new FieldMapping("id", "id"), new FieldMapping("address", "address", Address.class,
                        new FieldMapping("line1", "line1"), new FieldMapping("line2", "line2")),
                new FieldMapping("modeList", "modeList"));

    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
