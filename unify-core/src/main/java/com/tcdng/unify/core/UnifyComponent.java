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
package com.tcdng.unify.core;

/**
 * Interface that all unify components must implement.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface UnifyComponent {
    /**
     * Returns the component name.
     */
    String getName();

    /**
     * Returns the component node ID.
     */
    String getNodeId();

    /**
     * Returns this components component context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    UnifyComponentContext getUnifyComponentContext() throws UnifyException;

    /**
     * Initializes component with specified context.
     * 
     * @param unifyComponentContext
     *            the component context
     * @throws UnifyException
     *             if component is already initialized. If an error occurs.
     */
    void initialize(UnifyComponentContext unifyComponentContext) throws UnifyException;

    /**
     * Terminates component.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    void terminate() throws UnifyException;
    
    /**
     * Returns true if component is initialized.
     * 
     * @return true if initialized
     */
    boolean isInitialized();
}
