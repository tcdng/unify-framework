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

import org.junit.Test;

/**
 * JSON writer test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class JsonWriterTest {

    @Test
    public void testWriteBlankArray() throws Exception {
        String str = new JsonWriter()
                        .beginArray()
                        .endArray().toString();
        assertEquals("[]", str);
    }

    @Test(expected = RuntimeException.class)
    public void testWriteFieldInArray() throws Exception {
        new JsonWriter().beginArray().write("name", "John");
    }

    @Test(expected = RuntimeException.class)
    public void testOpenArrayInArray() throws Exception {
        new JsonWriter().beginArray().beginArray();
    }

    @Test
    public void testWriteBlankObject() throws Exception {
        String str = new JsonWriter()
                        .beginObject()
                        .endObject().toString();
        assertEquals("{}", str);
    }

    @Test
    public void testWriteSimpleObject() throws Exception {
        String str1 = new JsonWriter()
                        .beginObject()
                            .write("name", "Tom")
                        .endObject().toString();
        assertEquals("{\"name\":\"Tom\"}", str1);

        String str2 = new JsonWriter()
                        .beginObject()
                            .write("name", "Sam")
                            .write("age", 22)
                        .endObject().toString();
        assertEquals("{\"name\":\"Sam\",\"age\":22}", str2);

        String str3 = new JsonWriter()
                    .beginObject()
                        .write("name", "Sam")
                        .write("age", 22)
                        .write("prices", new double[] { 25.75, 13.4 })
                    .endObject().toString();
        assertEquals("{\"name\":\"Sam\",\"age\":22,\"prices\":[25.75,13.4]}", str3);
    }

    @Test
    public void testWriteSimpleObjectArray() throws Exception {
        String str1 = new JsonWriter()
                        .beginArray()
                            .beginObject()
                                .write("name", "Tom")
                            .endObject()
                        .endArray().toString();
        assertEquals("[{\"name\":\"Tom\"}]", str1);

        String str2 = new JsonWriter()
                        .beginArray()
                            .beginObject()
                                .write("name", "Tom")
                            .endObject()
                            .beginObject()
                                .write("name", "Harry")
                                .write("age", 22)
                            .endObject()
                        .endArray().toString();
        assertEquals("[{\"name\":\"Tom\"},{\"name\":\"Harry\",\"age\":22}]", str2);
    }

    @Test
    public void testWriteCompoundObject() throws Exception {
        String str1 = new JsonWriter()
                        .beginObject()
                            .write("name", "Tom")
                            .beginObject("contact")
                                .write("mobile", "+234010101")
                                .write("email", "m@tcdng.com")
                            .endObject()
                        .endObject()
                        .toString();
        assertEquals("{\"name\":\"Tom\",\"contact\":{\"mobile\":\"+234010101\",\"email\":\"m@tcdng.com\"}}", str1);

        String str2 = new JsonWriter()
                        .beginObject()
                            .write("name", "Tom")
                            .beginArray("contacts")
                                .beginObject()
                                    .write("mobile", "+234010101")
                                    .write("email", "m@tcdng.com")
                                .endObject()
                                .beginObject()
                                    .write("mobile", "+23455555")
                                    .write("email", "ken@tcdng.com")
                                .endObject()
                            .endArray()
                        .endObject()
                        .toString();
        assertEquals(
                "{\"name\":\"Tom\",\"contacts\":[{\"mobile\":\"+234010101\",\"email\":\"m@tcdng.com\"},{\"mobile\":\"+23455555\",\"email\":\"ken@tcdng.com\"}]}",
                str2);
    }

    @Test
    public void testCapacityExpansion() throws Exception {
        JsonWriter writer = new JsonWriter(1);
        assertEquals(1, writer.getCapacity());
        writer.beginObject()
                .write("name", "Tom")
                .beginObject("contact")
                    .write("mobile", "+234010101")
                    .write("email", "m@tcdng.com")
                .endObject()
            .endObject()
        .toString();
        assertEquals(2, writer.getCapacity());
    }

    @Test
    public void testWriteObject() throws Exception {
        Contact contact = new Contact("Jones", "+2348020000000", "24 Parklane Apapa", "Lagos, State");
        String str = new JsonWriter()
                        .beginObject()
                            .writeObject("contact", contact)
                        .endObject().toString();
        assertEquals("{\"contact\":{\"name\":\"Jones\",\"mobile\":\"+2348020000000\",\"addressLine\":[\"24 Parklane Apapa\",\"Lagos, State\"]}}", str);
    }

}
