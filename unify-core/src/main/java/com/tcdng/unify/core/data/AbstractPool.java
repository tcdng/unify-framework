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

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * An abstract generic pool that provides basic object pooling functionality.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractPool<T> {

    private Semaphore semaphore;

    private Queue<T> availableObjects;

    private Set<T> servedObjects;

    private long getTimeout;

    private int minSize;

    private boolean executeOnGet;

    public AbstractPool(final long getTimeout, final int minSize, final int maxSize) {
        this(getTimeout, minSize, maxSize, false);
    }

    public AbstractPool(final long getTimeout, final int minSize, final int maxSize, final boolean executeOnGet) {
        this.getTimeout = getTimeout;
        this.minSize = minSize;
        this.executeOnGet = executeOnGet;
        if (minSize > maxSize) {
            this.minSize = maxSize;
        }
        semaphore = new Semaphore(maxSize);
        availableObjects = new ConcurrentLinkedQueue<T>();
        servedObjects = new HashSet<T>();
    }

    public void initialize() throws UnifyException {
        try {
            for (int i = 0; i < minSize; i++) {
                availableObjects.add(createObject());
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.GENERIC_OBJECT_POOL_ERROR, e.getMessage());
        }
    }

    public void terminate() throws UnifyException {
        semaphore.drainPermits();
        for (T object : availableObjects) {
            destroyObject(object);
        }

        availableObjects.clear();
        servedObjects.clear();
    }

    public int available() {
        return semaphore.availablePermits();
    }

    public int size() {
        return availableObjects.size() + servedObjects.size();
    }

    public T borrowObject(Object... params) throws UnifyException {
        try {
            T object = null;
            if (semaphore.tryAcquire(getTimeout, TimeUnit.MILLISECONDS)) {
                object = availableObjects.poll();
                boolean createNew = object == null;

                if (!createNew) {
                    if (executeOnGet) {
                        try {
                            onGetObject(object, params);
                        } catch (Exception e) {
                            createNew = true;
                            destroyObject(object);
                        }
                    }
                }

                if (createNew) {
                    try {
                        object = createObject();
                        if (executeOnGet) {
                            onGetObject(object, params);
                        }
                    } catch (Exception e) {
                        semaphore.release();
                        if (object != null) {
                            destroyObject(object);
                        }
                        throw e;
                    }
                }
            }

            if (object == null) {
                throw new UnifyException(UnifyCoreErrorConstants.GENERIC_OBJECT_POOL_TIMEOUT);
            }

            servedObjects.add(object);
            return object;
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.GENERIC_OBJECT_POOL_ERROR, e.getMessage());
        }
    }

    public boolean returnObject(T object) {
        if (object != null && servedObjects.remove(object)) {
            if (availableObjects.offer(object)) {
                semaphore.release();
                return true;
            }
        }
        return false;
    }

    public boolean removeObject(T object) {
        if (availableObjects.remove(object)) {
            servedObjects.remove(object);
            semaphore.release();
            return true;
        }
        return false;
    }

    protected void onGetObject(T object, Object... params) throws Exception {

    }

    protected abstract T createObject(Object... params) throws Exception;

    protected abstract void destroyObject(T object);
}
