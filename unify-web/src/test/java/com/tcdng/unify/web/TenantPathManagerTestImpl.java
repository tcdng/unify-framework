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

package com.tcdng.unify.web;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Test tenant path manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("test-tenantpathmanager")
public class TenantPathManagerTestImpl extends AbstractUnifyComponent implements TenantPathManager {

    @Override
    public void verifyTenantPath(String tenantPath) throws UnifyException {
        if (!"/abcbank".equals(tenantPath)) {
            throw new UnifyOperationException();
        }
    }

    @Override
    public String getTenantCode(String tenantPath) throws UnifyException {
        return null;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
