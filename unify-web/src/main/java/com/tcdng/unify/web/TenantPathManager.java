/*
 * Copyright 2018-2023 The Code Department.
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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Tenant path manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface TenantPathManager extends UnifyComponent {

    /**
     * Verifies supplied tenant path.
     * 
     * @param tenantPath
     *            the tenant path to verify
     * @throws UnifyException
     *             if tenant path unknown. If an error occurs
     */
    void verifyTenantPath(String tenantPath) throws UnifyException;

    /**
     * Gets the tenant code assigned to tenant path.
     * 
     * @param tenantPath
     *            the tenant path
     * @return the assigned tenant code
     * @throws UnifyException
     *             if tenant path is unknown. If an error occurs
     */
    String getTenantCode(String tenantPath) throws UnifyException;
}
