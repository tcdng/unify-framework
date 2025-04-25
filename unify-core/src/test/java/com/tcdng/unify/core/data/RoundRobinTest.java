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
package com.tcdng.unify.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

/**
 * Round robin tests
 * 
 * @author The Code Department
 * @since 4.1
 */
public class RoundRobinTest {

    @Test(expected = RuntimeException.class)
    public void testEmptyRoundRobin() throws Exception {
    	TestRoundRobin robin = new TestRoundRobin(Collections.emptyList());
    	assertEquals(0, robin.size());
    	assertEquals(0, robin.position());
    	robin.next();
    }


    @Test
    public void testRoundRobin() throws Exception {
    	TestRoundRobin robin = new TestRoundRobin(Arrays.asList(new Account("T000001", BigDecimal.valueOf(34.25)),
    			new Account("T000002", BigDecimal.valueOf(750.00)),
    			new Account("T000003", BigDecimal.valueOf(132.50))));
    	assertEquals(3, robin.size());
    	assertEquals(0, robin.position());
    	
    	Account acc = robin.next();
    	assertEquals(1, robin.position());
    	assertNotNull(acc);
    	assertEquals("T000001", acc.getAccountNo());
    	assertEquals(BigDecimal.valueOf(34.25), acc.getBalance());
    	
    	acc = robin.next();
    	assertEquals(2, robin.position());
    	assertNotNull(acc);
    	assertEquals("T000002", acc.getAccountNo());
    	assertEquals(BigDecimal.valueOf(750.00), acc.getBalance());
    	
    	acc = robin.next();
    	assertEquals(0, robin.position());
    	assertNotNull(acc);
    	assertEquals("T000003", acc.getAccountNo());
    	assertEquals(BigDecimal.valueOf(132.50), acc.getBalance());
    	
    	acc = robin.next();
    	assertEquals(1, robin.position());
    	assertNotNull(acc);
    	assertEquals("T000001", acc.getAccountNo());
    	assertEquals(BigDecimal.valueOf(34.25), acc.getBalance());
    	
    	acc = robin.next();
    	assertEquals(2, robin.position());
    	assertNotNull(acc);
    	assertEquals("T000002", acc.getAccountNo());
    	assertEquals(BigDecimal.valueOf(750.00), acc.getBalance());
    	
    	acc = robin.next();
    	assertEquals(0, robin.position());
    	assertNotNull(acc);
    	assertEquals("T000003", acc.getAccountNo());
    	assertEquals(BigDecimal.valueOf(132.50), acc.getBalance());
    	
    	acc = robin.next();
    	assertEquals(1, robin.position());
    	assertNotNull(acc);
    	assertEquals("T000001", acc.getAccountNo());
    	assertEquals(BigDecimal.valueOf(34.25), acc.getBalance());
    }

}
