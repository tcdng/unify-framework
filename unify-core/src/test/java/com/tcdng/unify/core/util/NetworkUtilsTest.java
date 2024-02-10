/*
 * Copyright 2018-2024 The Code Department.
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
 * NetworkUtils test.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class NetworkUtilsTest {

    @Test
    public void testConstructURL() throws Exception {
        String urlA = NetworkUtils.constructURL("https", "localhost", (short) 0, "/unify", "/open");
        String urlB = NetworkUtils.constructURL("http", "localhost", (short) 7000, "/unifyweb", "/open");
        assertEquals("https://localhost/unify/open", urlA);
        assertEquals("http://localhost:7000/unifyweb/open", urlB);
    }
}
