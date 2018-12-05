/*
 * Copyright 2014 The Code Department
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

import com.tcdng.unify.core.UnifyException;

/**
 * Abstract implementation of a transform cache.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractTransformCache<T, U, V> extends AbstractCache<T, U> implements TransformCache<T, U, V> {

	@Override
	public V transformPut(T key, U object, long expirationTime) throws UnifyException {
		put(key, object, expirationTime);
		return transformCachedObject(object);
	}

	@Override
	public V getTransformed(T key) throws UnifyException {
		return transformCachedObject(get(key));
	}

	protected abstract V transformCachedObject(U cachedObject) throws UnifyException;
}
