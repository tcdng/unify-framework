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
package com.tcdng.unify.core.data;

import java.util.Date;

/**
 * Lock information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class LockInfo {

	private String lockName;

	private String currentOwner;

	private String threadId;

	private Date expiryTime;

	private Integer lockCount;

	public LockInfo(String lockName, String currentOwner, String threadId, Date expiryTime, Integer lockCount) {
		this.lockName = lockName;
		this.currentOwner = currentOwner;
		this.threadId = threadId;
		this.expiryTime = expiryTime;
		this.lockCount = lockCount;
	}

	public String getLockName() {
		return lockName;
	}

	public String getCurrentOwner() {
		return currentOwner;
	}

	public String getThreadId() {
		return threadId;
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public Integer getLockCount() {
		return lockCount;
	}

}
