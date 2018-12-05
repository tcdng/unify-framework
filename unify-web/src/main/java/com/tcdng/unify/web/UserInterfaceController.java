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
package com.tcdng.unify.web;

import com.tcdng.unify.core.UnifyException;

/**
 * User interface controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface UserInterfaceController extends Controller {

	/**
	 * Tests if bean is read-only.
	 * 
	 * @return a true value means that this controller is not populated with request
	 *         parameter values
	 */
	boolean isReadOnly();

	/**
	 * Resets the controller. This method is called by the controller manager before
	 * a call to the {@link #populate(String, String, String, Object)} method.
	 */
	void reset();

	/**
	 * Populates a controller property. This method is called by the framework for
	 * processing request parameters.
	 * 
	 * @param transferBlock
	 *            the transfer sequence
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void populate(DataTransferBlock transferBlock) throws UnifyException;
}
