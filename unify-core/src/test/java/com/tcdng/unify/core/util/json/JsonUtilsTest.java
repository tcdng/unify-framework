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

package com.tcdng.unify.core.util.json;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * JSON utilities tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class JsonUtilsTest {

    @Test
    public void testWriteStringField() throws Exception {
        String val = null;
        assertEquals("\"name\":null", JsonUtils.getFieldEntry("name", val));
        assertEquals("\"name\":\"John\"", JsonUtils.getFieldEntry("name", "John"));
        assertEquals("\"name\":\"John\\\"\"", JsonUtils.getFieldEntry("name", "John\""));
    }

    @Test
    public void testWriteStringArrayField() throws Exception {
        String[] val = null;
        assertEquals("\"names\":null", JsonUtils.getFieldEntry("names", val));
        assertEquals("\"names\":[]", JsonUtils.getFieldEntry("names", new String[] {}));
        assertEquals("\"names\":[\"John\"]", JsonUtils.getFieldEntry("names", new String[] { "John" }));
        assertEquals("\"names\":[\"John\",\"Mary\"]",
                JsonUtils.getFieldEntry("names", new String[] { "John", "Mary" }));
        assertEquals("\"names\":[\"John\",null]", JsonUtils.getFieldEntry("names", new String[] { "John", null }));
    }

    @Test
    public void testWriteNumberField() throws Exception {
        Integer val = null;
        assertEquals("\"age\":null", JsonUtils.getFieldEntry("age", val));
        assertEquals("\"age\":25", JsonUtils.getFieldEntry("age", Integer.valueOf(25)));
        assertEquals("\"price\":45.72", JsonUtils.getFieldEntry("price", BigDecimal.valueOf(45.72)));
    }

    @Test
    public void testWriteNumberArrayField() throws Exception {
        Integer[] val = null;
        assertEquals("\"ages\":null", JsonUtils.getFieldEntry("ages", val));
        assertEquals("\"ages\":[]", JsonUtils.getFieldEntry("ages", new Integer[] {}));
        assertEquals("\"ages\":[22]", JsonUtils.getFieldEntry("ages", new Integer[] { 22 }));
        assertEquals("\"ages\":[22,33]", JsonUtils.getFieldEntry("ages", new Integer[] { 22, 33 }));
        assertEquals("\"ages\":[22,null]", JsonUtils.getFieldEntry("ages", new Integer[] { 22, null }));
    }

    @Test
    public void testWriteBooleanField() throws Exception {
        Boolean val = null;
        assertEquals("\"openFlag\":null", JsonUtils.getFieldEntry("openFlag", val));
        assertEquals("\"openFlag\":true", JsonUtils.getFieldEntry("openFlag", Boolean.TRUE));
        assertEquals("\"openFlag\":false", JsonUtils.getFieldEntry("openFlag", Boolean.FALSE));
    }

    @Test
    public void testWriteBooleanArrayField() throws Exception {
        Boolean[] val = null;
        assertEquals("\"flags\":null", JsonUtils.getFieldEntry("flags", val));
        assertEquals("\"flags\":[]", JsonUtils.getFieldEntry("flags", new Boolean[] {}));
        assertEquals("\"flags\":[true]", JsonUtils.getFieldEntry("flags", new Boolean[] { true }));
        assertEquals("\"flags\":[true,false]", JsonUtils.getFieldEntry("flags", new Boolean[] { true, false }));
        assertEquals("\"flags\":[true,null]", JsonUtils.getFieldEntry("flags", new Boolean[] { true, null }));
    }

    @Test
    public void testWritePrimitiveField() throws Exception {
        assertEquals("\"code\":\"M\"", JsonUtils.getFieldEntry("code", 'M'));
        assertEquals("\"code\":22", JsonUtils.getFieldEntry("code", 22));
        assertEquals("\"code\":33", JsonUtils.getFieldEntry("code", 33L));
        assertEquals("\"code\":55", JsonUtils.getFieldEntry("code", (short) 55));
        assertEquals("\"code\":2.3", JsonUtils.getFieldEntry("code", (float) 2.3));
        assertEquals("\"code\":19.78", JsonUtils.getFieldEntry("code", 19.78));
        assertEquals("\"code\":true", JsonUtils.getFieldEntry("code", true));
    }

    @Test
    public void testWritePrimitiveArrayField() throws Exception {
        assertEquals("\"code\":[\"M\",\"A\"]", JsonUtils.getFieldEntry("code", new char[] { 'M', 'A' }));
        assertEquals("\"code\":[22,44]", JsonUtils.getFieldEntry("code", new int[] { 22, 44 }));
        assertEquals("\"code\":[33,66]", JsonUtils.getFieldEntry("code", new long[] { 33L, 66L }));
        assertEquals("\"code\":[55,77]", JsonUtils.getFieldEntry("code", new short[] { (short) 55, (short) 77 }));
        assertEquals("\"code\":[2.3,2.4,2.5]",
                JsonUtils.getFieldEntry("code", new float[] { (float) 2.3, (float) 2.4, (float) 2.5 }));
        assertEquals("\"code\":[19.78,19.76,19.81]",
                JsonUtils.getFieldEntry("code", new double[] { 19.78, 19.76, 19.81 }));
        assertEquals("\"code\":[true,false,true]",
                JsonUtils.getFieldEntry("code", new boolean[] { true, false, true }));
    }

}
