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
package com.tcdng.unify.web.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Web utilities test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class WebUtilsTest {

    @Test
    public void testGetBeanIndexedPathFromPath() throws Exception {
        assertEquals("/calculator:10/performAddition",
                WebUtils.generateBeanIndexedPathFromPath("/calculator/performAddition", 10));
    }

    @Test
    public void testGetPathFromBeanIndexedPath() throws Exception {
        assertEquals("/calculator/performAddition",
                WebUtils.extractPathFromBeanIndexedPath("/calculator:10/performAddition"));
        assertEquals("/calculator", WebUtils.extractPathFromBeanIndexedPath("/calculator:10"));
    }
}
