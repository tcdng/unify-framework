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
import java.util.Date;

import org.junit.Test;

import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.data.PackableDocConfig.FieldConfig;

/**
 * Packable document configuration builder tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class PackableDocConfigTest {

    @Test
    public void testBuilderSimpleWithNoBean() throws Exception {
        PackableDocConfig pdc = PackableDocConfig.newBuilder("addressConfig")
                .addFieldConfig("line1", DataType.STRING)
                .addFieldConfig("line2", DataType.STRING)
                .build();
        assertNotNull(pdc);
        assertEquals("addressConfig", pdc.getName());
        assertTrue(pdc.getBeanMappingClasses().isEmpty());

        FieldConfig fc = pdc.getFieldConfig("line1");
        assertNotNull(fc);
        assertEquals("line1", fc.getFieldName());
        assertEquals(String.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        fc = pdc.getFieldConfig("line2");
        assertNotNull(fc);
        assertEquals("line2", fc.getFieldName());
        assertEquals(String.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());
    }

    @Test
    public void testBuilderComplexWithNoBean() throws Exception {
        PackableDocConfig pdc = PackableDocConfig.newBuilder("customerConfig")
                .addFieldConfig("name", DataType.STRING)
                .addFieldConfig("birthDt", DataType.DATE)
                .addFieldConfig("id", DataType.LONG)
                .addFieldConfig("balance", DataType.DECIMAL)
                .addFieldConfig("modeList", DataType.STRING, true)
                .addComplexFieldConfig("address",
                        PackableDocConfig.newBuilder("addressConfig")
                                .addFieldConfig("line1", DataType.STRING)
                                .addFieldConfig("line2", DataType.STRING)
                                .build())
                .build();
        assertNotNull(pdc);
        assertEquals("customerConfig", pdc.getName());
        assertTrue(pdc.getBeanMappingClasses().isEmpty());

        FieldConfig fc = pdc.getFieldConfig("name");
        assertNotNull(fc);
        assertEquals("name", fc.getFieldName());
        assertEquals(String.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        fc = pdc.getFieldConfig("birthDt");
        assertNotNull(fc);
        assertEquals("birthDt", fc.getFieldName());
        assertEquals(Date.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        fc = pdc.getFieldConfig("balance");
        assertNotNull(fc);
        assertEquals("balance", fc.getFieldName());
        assertEquals(BigDecimal.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        fc = pdc.getFieldConfig("id");
        assertNotNull(fc);
        assertEquals("id", fc.getFieldName());
        assertEquals(Long.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        fc = pdc.getFieldConfig("address");
        assertNotNull(fc);
        assertEquals("address", fc.getFieldName());
        assertNull(fc.getDataType());
        assertNotNull(fc.getPackableDocConfig());
        assertTrue(fc.isComplex());
        assertFalse(fc.isList());

        PackableDocConfig pdc2 = fc.getPackableDocConfig();

        fc = pdc.getFieldConfig("modeList");
        assertNotNull(fc);
        assertEquals("modeList", fc.getFieldName());
        assertEquals(String.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertTrue(fc.isList());

        // Complex field
        assertEquals("addressConfig", pdc2.getName());
        assertTrue(pdc2.getBeanMappingClasses().isEmpty());

        FieldConfig fc2 = pdc2.getFieldConfig("line1");
        assertNotNull(fc2);
        assertEquals("line1", fc2.getFieldName());
        assertEquals(String.class, fc2.getDataType());
        assertNull(fc2.getPackableDocConfig());
        assertFalse(fc2.isComplex());
        assertFalse(fc2.isList());

        fc2 = pdc2.getFieldConfig("line2");
        assertNotNull(fc);
        assertEquals("line2", fc2.getFieldName());
        assertEquals(String.class, fc2.getDataType());
        assertNull(fc2.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc2.isList());
    }

    @Test
    public void testBuildFromSimpleBean() throws Exception {
        PackableDocConfig pdc = PackableDocConfig.buildFrom("addressConfig", Address.class);
        assertNotNull(pdc);
        assertEquals("addressConfig", pdc.getName());

        FieldConfig fc = pdc.getFieldConfig("line1");
        assertNotNull(fc);
        assertEquals("line1", fc.getFieldName());
        assertEquals(String.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        fc = pdc.getFieldConfig("line2");
        assertNotNull(fc);
        assertEquals("line2", fc.getFieldName());
        assertEquals(String.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        BeanMappingConfig bmc = pdc.getBeanMapping(Address.class);
        assertNotNull(bmc);
        assertEquals("line1", bmc.getMappedField("line1"));
        assertEquals("line2", bmc.getMappedField("line2"));
    }

    @Test
    public void testBuildFromComplexBean() throws Exception {
        PackableDocConfig pdc = PackableDocConfig.buildFrom("customerConfig", Customer.class);
        assertNotNull(pdc);

        assertEquals("customerConfig", pdc.getName());

        FieldConfig fc = pdc.getFieldConfig("name");
        assertNotNull(fc);
        assertEquals("name", fc.getFieldName());
        assertEquals(String.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        fc = pdc.getFieldConfig("birthDt");
        assertNotNull(fc);
        assertEquals("birthDt", fc.getFieldName());
        assertEquals(Date.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        fc = pdc.getFieldConfig("balance");
        assertNotNull(fc);
        assertEquals("balance", fc.getFieldName());
        assertEquals(BigDecimal.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        fc = pdc.getFieldConfig("id");
        assertNotNull(fc);
        assertEquals("id", fc.getFieldName());
        assertEquals(Long.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc.isList());

        fc = pdc.getFieldConfig("address");
        assertNotNull(fc);
        assertEquals("address", fc.getFieldName());
        assertNull(fc.getDataType());
        assertNotNull(fc.getPackableDocConfig());
        assertTrue(fc.isComplex());
        assertFalse(fc.isList());

        PackableDocConfig pdc2 = fc.getPackableDocConfig();

        fc = pdc.getFieldConfig("modeList");
        assertNotNull(fc);
        assertEquals("modeList", fc.getFieldName());
        assertEquals(String.class, fc.getDataType());
        assertNull(fc.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertTrue(fc.isList());

        BeanMappingConfig bmc = pdc.getBeanMapping(Customer.class);
        assertNotNull(bmc);
        assertEquals("name", bmc.getMappedField("name"));
        assertEquals("birthDt", bmc.getMappedField("birthDt"));
        assertEquals("balance", bmc.getMappedField("balance"));
        assertEquals("id", bmc.getMappedField("id"));
        assertEquals("address", bmc.getMappedField("address"));
        assertEquals("modeList", bmc.getMappedField("modeList"));

        // Complex field
        assertEquals("customerConfig.address", pdc2.getName());

        FieldConfig fc2 = pdc2.getFieldConfig("line1");
        assertNotNull(fc2);
        assertEquals("line1", fc2.getFieldName());
        assertEquals(String.class, fc2.getDataType());
        assertNull(fc2.getPackableDocConfig());
        assertFalse(fc2.isComplex());
        assertFalse(fc2.isList());

        fc2 = pdc2.getFieldConfig("line2");
        assertNotNull(fc);
        assertEquals("line2", fc2.getFieldName());
        assertEquals(String.class, fc2.getDataType());
        assertNull(fc2.getPackableDocConfig());
        assertFalse(fc.isComplex());
        assertFalse(fc2.isList());

        BeanMappingConfig bmc2 = pdc2.getBeanMapping(Address.class);
        assertNotNull(bmc2);
        assertEquals("line1", bmc2.getMappedField("line1"));
        assertEquals("line2", bmc2.getMappedField("line2"));
    }
}
