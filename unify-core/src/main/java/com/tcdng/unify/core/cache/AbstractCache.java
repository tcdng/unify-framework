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

import java.util.Iterator;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Expirable;
import com.tcdng.unify.core.data.FactoryMap;

/**
 * An abstract class that implements the base functionality of a cache.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractCache<T, U> extends AbstractUnifyComponent implements Cache<T, U> {

	@Configurable("60")
	private int defaultExpiryPeriod;

	private FactoryMap<T, CacheEntry> cacheEntries;

	public AbstractCache() {
		cacheEntries = new FactoryMap<T, CacheEntry>() {
			@SuppressWarnings("unchecked")
			@Override
			protected CacheEntry create(T key, Object... params) throws Exception {
				return new CacheEntry((U) params[0], (Long) params[1]);
			}
		};
	}

	@Override
	public U put(T key, U object) throws UnifyException {
		return put(key, object, defaultExpiryPeriod);
	}

	@Override
	public U put(T key, U object, long expiryPeriod) throws UnifyException {
		cacheEntries.remove(key);
		cacheEntries.get(key, object, expiryPeriod);
		return object;
	}

	@Override
	public U get(T key) throws UnifyException {
		if (cacheEntries.isKey(key)) {
			return cacheEntries.get(key).getObject();
		}
		return null;
	}

	@Override
	public U remove(T key) throws UnifyException {
		return cacheEntries.remove(key).getObject();
	}

	@Override
	public void clear() throws UnifyException {
		cacheEntries.clear();
	}

	@Override
	public int size() {
		return cacheEntries.size();
	}

	@Override
	@Expirable(cycleInSec = 20)
	public void removeExpiredCacheEntries() throws UnifyException {
		long currentTime = System.currentTimeMillis();
		for (Iterator<Map.Entry<T, CacheEntry>> it = cacheEntries.entrySet().iterator(); it.hasNext();) {
			Map.Entry<T, CacheEntry> entry = it.next();
			if (currentTime >= entry.getValue().getTimeToExpire()) {
				it.remove();
			}
		}
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	private class CacheEntry {

		private U object;

		private long expiryPeriod;

		private long timeToExpire;

		public CacheEntry(U object, long expiryPeriod) {
			this.expiryPeriod = expiryPeriod * 1000L;
			this.object = object;
			getObject();
		}

		public U getObject() {
			timeToExpire = System.currentTimeMillis() + expiryPeriod;
			return object;
		}

		public long getTimeToExpire() {
			return timeToExpire;
		}
	}
}
