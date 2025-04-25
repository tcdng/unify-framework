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
package com.tcdng.unify.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * FileUtils test.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class FileUtilsTest {

    @Test
	public void testExtendFileNameNull() throws Exception {
		assertNull(FileUtils.detectPresentAndGetThemeFileName(null, null, null));
		assertNull(FileUtils.detectPresentAndGetThemeFileName(null, "-blue", null));

		assertEquals("theme.png", FileUtils.detectPresentAndGetThemeFileName("theme.png", null, null));
		assertEquals("/src/resources/theme.png",
				FileUtils.detectPresentAndGetThemeFileName("/src/resources/theme.png", null, null));
	}

}
