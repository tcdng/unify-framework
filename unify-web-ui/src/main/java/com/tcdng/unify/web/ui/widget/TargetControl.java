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

package com.tcdng.unify.web.ui.widget;

import com.tcdng.unify.core.UnifyException;

/**
 * Control with target element.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface TargetControl extends Control {

    /**
     * Returns target control's static binding value.
     * 
     * @return the value if set otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    String getStaticBindingValue() throws UnifyException;

    /**
     * Returns the target element Id.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    String getTargetId() throws UnifyException;

	/**
	 * Indicated is message resolution is required
	 * 
	 * @return the return
	 * @throws UnifyException if an error occurs
	 */
	boolean isResolve() throws UnifyException;
    
    /**
     * Returns control's debounce flag.
     * 
     * @return the debounce flag
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isDebounce() throws UnifyException;

    /**
     * Indicates if target control should always bind value to value store index.
     * 
     * @return true if bind to value store index otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isAlwaysValueIndex() throws UnifyException;
}
