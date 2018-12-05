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

package com.tcdng.unify.core.task;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Task ID generator.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface TaskIdGenerator extends UnifyComponent {

	/**
	 * Generates a task ID based on task input.
	 * 
	 * @param taskInput
	 *            the task input to use
	 * @return the generated ID
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String generateID(TaskInput taskInput) throws UnifyException;
}
