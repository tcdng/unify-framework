/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Application context tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ApplicationContextTest {

    private ApplicationContext applicationContext;

    @Before
    public void setup() throws Exception {
        applicationContext = new ApplicationContext(null, null, "\n");
    }

    @Test
    public void testContextHasNoPrivilegesForRole() {
        assertFalse(applicationContext.isRoleAttributes(""));
    }

    @Test
    public void testContextHasPrivilegesForRole() {
        String roleCode = String.valueOf(1L);
        applicationContext.setRoleAttributes(roleCode, new RoleAttributes());
        assertTrue(applicationContext.isRoleAttributes(roleCode));
    }
}
