/*
 * Copyright (c) 2018-2025 The Code Department.
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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * IO utilities test.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class IOUtilsTest {

	@Test
	public void testGetParentDirectory() throws Exception {
		final String separator = System.getProperty("file.separator");
		assertEquals("", IOUtils.getParentDirectory("actualize"));
		assertEquals("", IOUtils.getParentDirectory("actualize/"));
		assertEquals("actualize" + separator, IOUtils.getParentDirectory("actualize/src"));
		assertEquals("actualize" + separator, IOUtils.getParentDirectory("actualize/src/"));
		assertEquals("actualize" + separator + "src" + separator, IOUtils.getParentDirectory("actualize/src/java"));
	}
	
    @Test
	public void testIsWithProtocolInfix() throws Exception {
		assertFalse(IOUtils.isWithProtocolInfix(null));
		assertFalse(IOUtils.isWithProtocolInfix(""));
		assertFalse(IOUtils.isWithProtocolInfix("c"));
		assertFalse(IOUtils.isWithProtocolInfix("test.txt"));
		assertFalse(IOUtils.isWithProtocolInfix("/samples/test.txt"));
		assertFalse(IOUtils.isWithProtocolInfix("\\samples\\test.txt"));
		
		assertTrue(IOUtils.isWithProtocolInfix("file://etc/passwd"));
		assertTrue(IOUtils.isWithProtocolInfix("http://etc/passwd"));
		assertTrue(IOUtils.isWithProtocolInfix("https://etc/passwd"));
		assertTrue(IOUtils.isWithProtocolInfix("ws://etc/passwd"));
	}
	
    @Test
    public void testBuildFilename() throws Exception {
        String fileSeparator = System.getProperty("file.separator");

        assertEquals(fileSeparator, IOUtils.buildFilename(null, null));

        assertEquals("c:" + fileSeparator + "home" + fileSeparator, IOUtils.buildFilename("c:\\home", null));
        assertEquals(IOUtils.buildFilename("c:/home", null), IOUtils.buildFilename("c:\\home", null));
        assertEquals(IOUtils.buildFilename("c:/home\\", null), IOUtils.buildFilename("c:\\home", null));

        assertEquals(fileSeparator + "accounts.txt", IOUtils.buildFilename(null, "accounts.txt"));
        assertEquals(fileSeparator + "accounts.txt", IOUtils.buildFilename(null, "\\accounts.txt"));
        assertEquals(fileSeparator + "accounts.txt", IOUtils.buildFilename(null, "/accounts.txt"));

        String expectedFilename = "c:" + fileSeparator + "home" + fileSeparator + "accounts.txt";
        assertEquals(expectedFilename, IOUtils.buildFilename("c:\\home", "accounts.txt"));
        assertEquals(expectedFilename, IOUtils.buildFilename("c:\\home", "/accounts.txt"));
        assertEquals(expectedFilename, IOUtils.buildFilename("c:\\home/", "\\accounts.txt"));
    }
    
    @Test
    public void testActualFilename() throws Exception {
    	assertEquals("test.txt", IOUtils.getActualFileName("test.txt"));
    	assertEquals("test.txt", IOUtils.getActualFileName("/test.txt"));
    	assertEquals("test.txt", IOUtils.getActualFileName("c:/test.txt"));
    	assertEquals("test.txt", IOUtils.getActualFileName("c:/case/test.txt"));
    	assertEquals("test.txt", IOUtils.getActualFileName("/case/test.txt"));
    	
    	assertEquals("test.txt", IOUtils.getActualFileName("test.txt"));
    	assertEquals("test.txt", IOUtils.getActualFileName("\\test.txt"));
    	assertEquals("test.txt", IOUtils.getActualFileName("c:\\test.txt"));
    	assertEquals("test.txt", IOUtils.getActualFileName("c:\\case\\test.txt"));
    	assertEquals("test.txt", IOUtils.getActualFileName("\\case\\test.txt"));
    }
    
    @Test
    public void testIsClassLoaderResource() throws Exception {
    	assertFalse(IOUtils.isClassLoaderResource("/banner/banner.txt"));
    	assertFalse(IOUtils.isClassLoaderResource("/banner.txt"));
    	assertTrue(IOUtils.isClassLoaderResource("banner/banner.txt"));
    	assertTrue(IOUtils.isClassLoaderResource("banner.txt"));
    }
}
