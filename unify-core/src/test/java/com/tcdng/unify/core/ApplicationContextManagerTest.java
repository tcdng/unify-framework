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
package com.tcdng.unify.core;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Application context manager tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class ApplicationContextManagerTest extends AbstractUnifyComponentTest {

    @Test
    public void testLoadRequestContext() throws Exception {
        RequestContextManager requestContextManager =
                (RequestContextManager) getComponent(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER);
        requestContextManager.reset();
        requestContextManager.loadRequestContext(null);
        assertNotNull(requestContextManager.getRequestContext());
    }

    @Test
    public void testUnloadRequestContext() throws Exception {
        RequestContextManager requestContextManager =
                (RequestContextManager) getComponent(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER);
        requestContextManager.reset();
        requestContextManager.loadRequestContext(null);
        assertNotNull(requestContextManager.getRequestContext());
        requestContextManager.unloadRequestContext();
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
