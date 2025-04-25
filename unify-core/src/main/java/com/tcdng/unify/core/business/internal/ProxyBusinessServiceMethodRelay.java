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
package com.tcdng.unify.core.business.internal;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Expirable;
import com.tcdng.unify.core.annotation.Taskable;

/**
 * Proxy business service method information relay.
 * 
 * @author Lateef
 * @since 4.1
 */
public interface ProxyBusinessServiceMethodRelay extends UnifyComponent {

    /**
     * Returns taskable annotation for method with supplied signature if found.
     * 
     * @param signature
     *            the supplied signature
     * @throws UnifyException
     *             if an error occur
     */
    Taskable getTaskable(String signature) throws UnifyException;

    /**
     * Returns expirable annotation for method with supplied signature if found.
     * 
     * @param signature
     *            the supplied signature
     * @throws UnifyException
     *             if an error occur
     */
    Expirable getExpirable(String signature) throws UnifyException;
}
