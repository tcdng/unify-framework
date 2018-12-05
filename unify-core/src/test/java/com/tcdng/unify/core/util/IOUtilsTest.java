/*
 * Copyright 2014 The Code Department
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

import org.junit.Test;

/**
 * IO utilities test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class IOUtilsTest {

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
}
