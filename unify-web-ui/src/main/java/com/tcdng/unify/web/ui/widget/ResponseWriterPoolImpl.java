/*
 * Copyright 2018-2020 The Code Department.
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

import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.AbstractPool;
import com.tcdng.unify.core.upl.UplComponent;
import com.tcdng.unify.core.upl.UplComponentWriter;
import com.tcdng.unify.core.upl.UplComponentWriterManager;
import com.tcdng.unify.web.ClientRequest;
import com.tcdng.unify.web.ui.WebUIApplicationComponents;

/**
 * Default implementation of response writer pool.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebUIApplicationComponents.APPLICATION_RESPONSEWRITERPOOL)
public class ResponseWriterPoolImpl extends AbstractUnifyComponent implements ResponseWriterPool {

    @Configurable
    private UplComponentWriterManager uplComponentWriterManager;

    @Configurable("4000") // 4 seconds
    private long getTimeout;

    @Configurable("256")
    private int maxSize;

    @Configurable("8")
    private int minSize;

    private InternalPool internalPool;

    @Override
    public ResponseWriter getResponseWriter(ClientRequest clientRequest) throws UnifyException {
        return internalPool.borrowObject(uplComponentWriterManager.getWriters(clientRequest.getClientPlatform()));
    }

    @Override
    public boolean restore(ResponseWriter writer) throws UnifyException {
        return internalPool.returnObject(writer);
    }

    @Override
    protected void onInitialize() throws UnifyException {
        internalPool = new InternalPool();
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
            return (ResponseWriter) getComponent(WebUIApplicationComponents.APPLICATION_RESPONSEWRITER);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onGetObject(ResponseWriter responseWriter, Object... params) throws Exception {
            responseWriter.reset((Map<Class<? extends UplComponent>, UplComponentWriter>) params[0]);
        }

        @Override
        protected void destroyObject(ResponseWriter responseWriter) {

        }
    }
}
