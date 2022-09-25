/*
 * Copyright 2018-2022 The Code Department.
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
 * Accounting utilities tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class AccountingUtilsTest {

    @Test
    public void testMakeParsableNegativeAmount() throws Exception {
        assertNull(AccountingUtils.makeParsableNegativeAmount(null));
        assertEquals("10", AccountingUtils.makeParsableNegativeAmount("10"));
        assertEquals("(10)", AccountingUtils.makeParsableNegativeAmount("(10)"));
        assertEquals("(10)", AccountingUtils.makeParsableNegativeAmount("(10"));
        assertEquals("(10)", AccountingUtils.makeParsableNegativeAmount("10)"));
        assertEquals("1.25", AccountingUtils.makeParsableNegativeAmount("1.25"));
        assertEquals("(1.25)", AccountingUtils.makeParsableNegativeAmount("(1.25)"));
        assertEquals("(1.25)", AccountingUtils.makeParsableNegativeAmount("(1.25"));
        assertEquals("(1.25)", AccountingUtils.makeParsableNegativeAmount("1.25)"));
    }

}
