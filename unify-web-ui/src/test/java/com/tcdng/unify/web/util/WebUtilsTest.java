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

package com.tcdng.unify.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.tcdng.unify.web.ui.DataTransferHeader;
import com.tcdng.unify.web.ui.util.WebUtils;

/**
 * Web utilities tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class WebUtilsTest {

	DataTransferHeader header = new DataTransferHeader(null);

	@Test
	public void testAddParameterToPathNull() throws Exception {
		assertNull(WebUtils.addParameterToPath(null, null, null));
		assertNull(WebUtils.addParameterToPath(null, "prm", null));
		assertNull(WebUtils.addParameterToPath(null, null, "2"));
		assertNull(WebUtils.addParameterToPath(null, "prm", "2"));
	}

	@Test
	public void testAddParameterToPath() throws Exception {
		assertEquals("https://localhost:8084", WebUtils.addParameterToPath("https://localhost:8084", null, "2"));
		assertEquals("https://localhost:8084?prm=", WebUtils.addParameterToPath("https://localhost:8084", "prm", null));
		assertEquals("https://localhost:8084?prm=2", WebUtils.addParameterToPath("https://localhost:8084", "prm", "2"));

		assertEquals("https://localhost:8084?age=32",
				WebUtils.addParameterToPath("https://localhost:8084?age=32", null, "2"));
		assertEquals("https://localhost:8084?age=32&prm=",
				WebUtils.addParameterToPath("https://localhost:8084?age=32", "prm", null));
		assertEquals("https://localhost:8084?age=32&prm=2",
				WebUtils.addParameterToPath("https://localhost:8084?age=32", "prm", "2"));

		assertEquals("https://localhost:8084?age=32&color=red",
				WebUtils.addParameterToPath("https://localhost:8084?age=32&color=red", null, "2"));
		assertEquals("https://localhost:8084?age=32&color=red&prm=",
				WebUtils.addParameterToPath("https://localhost:8084?age=32&color=red", "prm", null));
		assertEquals("https://localhost:8084?age=32&color=red&prm=2",
				WebUtils.addParameterToPath("https://localhost:8084?age=32&color=red", "prm", "2"));
	}

}
