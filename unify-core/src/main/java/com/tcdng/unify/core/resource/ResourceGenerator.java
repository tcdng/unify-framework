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

package com.tcdng.unify.core.resource;

import java.io.OutputStream;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * A component the generates a resource. Resource generators are typically
 * non-singletons.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ResourceGenerator<T> extends UnifyComponent {

    /**
     * Generates resource and writes to supplied output stream.
     * 
     * @param outputStream
     *            the stream to write to
     * @return the generate resource category
     * @throws UnifyException
     *             if an error occurs
     */
    T generate(OutputStream outputStream) throws UnifyException;

    /**
     * Returns true resource generator is ready for generation.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isReady() throws UnifyException;
}
