/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.web.ui;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.UserPlatform;
import com.tcdng.unify.core.data.AbstractPool;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.web.WebApplicationComponents;

/**
 * Default implementation of response writer pool.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_RESPONSEWRITERPOOL)
public class ResponseWriterPoolImpl extends AbstractUnifyComponent implements ResponseWriterPool {

    @Configurable("2000") // 2 seconds
    private long getTimeout;

    @Configurable("128")
    private int maxSize;

    @Configurable("8")
    private int minSize;

    private FactoryMap<UserPlatform, InternalPool> internalPools = new FactoryMap<UserPlatform, InternalPool>() {

        @Override
        protected InternalPool create(UserPlatform key, Object... params) throws Exception {
            return new InternalPool();
        }

    };

    @Override
    public ResponseWriter getResponseWriter() throws UnifyException {
        return internalPools.get(getSessionContext().getPlatform()).borrowObject();
    }

    @Override
    public boolean restore(ResponseWriter writer) throws UnifyException {
        return internalPools.get(getSessionContext().getPlatform()).returnObject(writer);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private class InternalPool extends AbstractPool<ResponseWriter> {

        public InternalPool() {
            super(getTimeout, minSize, maxSize, true);
        }

        @Override
        protected ResponseWriter createObject(Object... params) throws Exception {
            return (ResponseWriter) getComponent(WebApplicationComponents.APPLICATION_RESPONSEWRITER);
        }

        @Override
        protected void onGetObject(ResponseWriter writer, Object... params) throws Exception {
            writer.reset();
        }

        @Override
        protected void destroyObject(ResponseWriter object) {

        }
    }
}
