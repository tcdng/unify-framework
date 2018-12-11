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
package com.tcdng.unify.core.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;

/**
 * Cache tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class CacheTest extends AbstractUnifyComponentTest {

	private Cache<String, String> cache;

	@Test
	public void testPutObjectWithDefaultExpirationInCache() throws Exception {
		cache.put("address", "24 Parklane, Apapa");
		cache.put("telephone", "+2348020948192");
		assertEquals(2, cache.size());
	}

	@Test
	public void testPutObjectWithSpecifiedExpirationInCache() throws Exception {
		cache.put("address", "24 Parklane, Apapa", 10000);
		assertEquals(1, cache.size());
	}

	@Test
	public void testGetCachedObject() throws Exception {
		cache.put("address", "24 Parklane, Apapa");
		assertEquals("24 Parklane, Apapa", cache.get("address"));
	}

	@Test
	public void testReplaceCachedObjectWithPut() throws Exception {
		cache.put("telephone", "+2348020948192");
		cache.put("address", "24 Parklane, Apapa");
		cache.put("address", "38 Warehouse Rd, Apapa");
		assertEquals(2, cache.size());
		assertEquals("+2348020948192", cache.get("telephone"));
		assertEquals("38 Warehouse Rd, Apapa", cache.get("address"));
	}

	@Test
	public void testRemoveCachedObject() throws Exception {
		cache.put("telephone", "+2348020948192");
		cache.put("address", "24 Parklane, Apapa");
		assertEquals("+2348020948192", cache.remove("telephone"));
		assertEquals(1, cache.size());
		assertNull(cache.get("telephone"));
		assertEquals("24 Parklane, Apapa", cache.get("address"));
	}

	@Test
	public void testRemoveAllCachedObjects() throws Exception {
		cache.put("telephone", "+2348020948192");
		cache.put("address", "24 Parklane, Apapa");
		cache.clear();
		assertEquals(0, cache.size());
		assertNull(cache.get("telephone"));
		assertNull(cache.get("address"));
	}

	@Test
	public void testRemoveExpiredCachedObjects() throws Exception {
		cache.put("telephone", "+2348020948192", -1); // Simulate expired
		cache.put("address", "24 Parklane, Apapa");
		assertEquals(2, cache.size());
		cache.removeExpiredCacheEntries();
		assertEquals(1, cache.size());
		assertNull(cache.get("telephone"));
		assertEquals("24 Parklane, Apapa", cache.get("address"));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onSetup() throws Exception {
		cache = (Cache<String, String>) getComponent("mock-cache");
		cache.clear();
	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
