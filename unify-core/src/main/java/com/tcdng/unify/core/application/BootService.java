/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.core.application;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessService;

/**
 * Used to manage bootup and shutdown sequence.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface BootService extends BusinessService {

    /**
     * Executes start sequence.
     * 
     * @throws UnifyException
     *             if an error roccurs
     */
    void startup() throws UnifyException;

    /**
     * Executes shutdown sequence.
     * 
     * @throws UnifyException
     *             if an error roccurs
     */
    void shutdown() throws UnifyException;
}
