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
package com.tcdng.unify.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * TasgUtils tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TagUtilTest {

    @Test
    public void testIsStringTag() throws Exception {
        assertTrue(TokenUtils.isStringToken("$s{}"));
        assertTrue(TokenUtils.isStringToken("$s{hello}"));
        assertFalse(TokenUtils.isStringToken("$s{sky"));
        assertFalse(TokenUtils.isStringToken("s{hello}"));
    }

    @Test
    public void testIsRequestPathActionTag() throws Exception {
        assertTrue(TokenUtils.isRequestPathActionTag("$r{}"));
        assertTrue(TokenUtils.isRequestPathActionTag("$r{hello.world}"));
        assertFalse(TokenUtils.isRequestPathActionTag("$s{sky}"));
        assertFalse(TokenUtils.isRequestPathActionTag("s{hello}"));
    }

    @Test
    public void testGetStringTagValue() throws Exception {
        assertEquals("", TokenUtils.getStringTokenValue("$s{}"));
        assertEquals("hello", TokenUtils.getStringTokenValue("$s{hello}"));
        assertEquals("$s{sky", TokenUtils.getStringTokenValue("$s{sky"));
        assertEquals("s{hello}", TokenUtils.getStringTokenValue("s{hello}"));
    }

    @Test
    public void testExtractTagValue() throws Exception {
        assertEquals("tcdng.com", TokenUtils.extractTokenValue("$m{tcdng.com}"));
        // Bellow should also pass since method extractTagValue assumes supplied
        // parameter is a valid tagged value
        assertEquals("ne", TokenUtils.extractTokenValue("Skynet"));
    }
}
