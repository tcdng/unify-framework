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
package com.tcdng.unify.core.business;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Convenient base class for queued execution.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractQueuedExec<T> implements QueuedExec<T> {

	private final ExecutorService executor;
	
	public AbstractQueuedExec(int maxProcessingThreads) {
		this.executor = Executors.newFixedThreadPool(maxProcessingThreads);
	}
	
	@Override
	public final void execute(T param) {
		executor.execute(new ExecThread(param));
	}
	
	protected abstract void doExecute(T param);
	
	private class ExecThread implements Runnable {

		private final T param;

		public ExecThread(T param) {
			this.param = param;
		}

		@Override
		public void run() {
			doExecute(param);
		}
	}
	
}
