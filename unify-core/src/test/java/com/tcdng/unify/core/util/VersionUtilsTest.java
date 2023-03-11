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
package com.tcdng.unify.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Version utils tests
 * 
 * @author The Code Department
 * @since 1.0
 */
public class VersionUtilsTest {

    @Test
    public void testIsNewerVersion() throws Exception {
        assertTrue(VersionUtils.isNewerVersion("1.1", "1.0"));
        assertTrue(VersionUtils.isNewerVersion("1.1", "1.0.5"));
        assertTrue(VersionUtils.isNewerVersion("1.0.4", "1.0.3"));
        assertTrue(VersionUtils.isNewerVersion("1.1", "1.0.0.16"));
        assertTrue(VersionUtils.isNewerVersion("1.5.1.12", "1.5.0.17"));
    }

    @Test
    public void testIsNotNewerVersion() throws Exception {
        assertFalse(VersionUtils.isNewerVersion("1.0", "1.0"));
        assertFalse(VersionUtils.isNewerVersion("1.1", "1.1.5"));
        assertFalse(VersionUtils.isNewerVersion("1.2.32", "1.3.3"));
        assertFalse(VersionUtils.isNewerVersion("1.0.0.2", "1.0.0.16"));
        assertFalse(VersionUtils.isNewerVersion("1.5.0.17", "1.5.0.17"));
    }
}
