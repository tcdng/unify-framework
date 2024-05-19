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
package com.tcdng.unify.core.system;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Cluster lock manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ClusterLockManager extends UnifyComponent {

	/**
	 * Grabs lock if available.
	 * 
	 * @param lockName the lock name
	 * @return true if lock is grabbed otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean tryGrabLock(String lockName) throws UnifyException;

	/**
	 * Grabs lock. Waits for lock to be available.
	 * 
	 * @param lockName the lock name
	 * @param timeout  the timeout (no timeout if egetive or zero)
	 * @return true if lock is grabbed otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean grabLock(String lockName, long timeout) throws UnifyException;

	/**
	 * Releases lock.
	 * 
	 * @param lockName the lock name
	 * @throws UnifyException if an error occurs
	 */
	void releaseLock(String lockName) throws UnifyException;

}
