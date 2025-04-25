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
package com.tcdng.unify.web.ui.widget.writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.upl.UplComponentWriter;
import com.tcdng.unify.web.ui.widget.Behavior;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Behavior writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface BehaviorWriter extends UplComponentWriter {

	/**
	 * Writes behavior for a specified component.
	 * 
	 * @param writer         the response writer
	 * @param behavior       the behavior to write
	 * @param id             the component id
	 * @param cmdTag         optional command tag
	 * @param preferredEvent optional preferred event
	 * @throws UnifyException if an error occurs
	 */
	void writeBehavior(ResponseWriter writer, Behavior behavior, String id, String cmdTag, String preferredEvent)
			throws UnifyException;
}
