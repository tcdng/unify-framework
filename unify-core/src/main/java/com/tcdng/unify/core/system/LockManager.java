/*
 * Copyright 2018-2025 The Code Department.
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
import com.tcdng.unify.core.data.LockInfo;

/**
 * Lock manager.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface LockManager extends UnifyComponent {

	/**
	 * Checks if lock is locked.
	 * 
	 * @param lockName the lock name
	 * @return true if locked otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean isLocked(String lockName) throws UnifyException;
	
	/**
	 * Grabs lock if available.
	 * 
	 * @param lockName the lock name
	 * @return true if lock is grabbed otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean tryGrabLock(String lockName) throws UnifyException;
	
	/**
	 * Grabs lock with no timeout.
	 * @param lockName the lock name
	 * @return true if lock is grabbed otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean grabLock(String lockName) throws UnifyException;

	/**
	 * Grabs lock. Waits for lock to be available.
	 * 
	 * @param lockName the lock name
	 * @param timeout  the timeout (no timeout if negetive or zero)
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

	/**
	 * Gets lock information.
	 * 
	 * @param lockName the lock name
	 * @return the lock information
	 * @throws UnifyException if an error occurs
	 */
	LockInfo getLockInfo(String lockName) throws UnifyException;

}
