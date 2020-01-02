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
package com.tcdng.unify.web.ui;

import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.AbstractPool;
import com.tcdng.unify.core.upl.UplComponent;
import com.tcdng.unify.core.upl.UplComponentWriter;
import com.tcdng.unify.web.UnifyWebSessionAttributeConstants;
import com.tcdng.unify.web.WebApplicationComponents;

/**
 * Default implementation of response writer pool.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_RESPONSEWRITERPOOL)
public class ResponseWriterPoolImpl extends AbstractUnifyComponent implements ResponseWriterPool {

    @Configurable("4000") // 4 seconds
    private long getTimeout;

    @Configurable("256")
    private int maxSize;

    @Configurable("8")
    private int minSize;

    private InternalPool internalPool;

    @SuppressWarnings("unchecked")
    @Override
    public ResponseWriter getResponseWriter() throws UnifyException {
        Map<Class<? extends UplComponent>, UplComponentWriter> writers =
                (Map<Class<? extends UplComponent>, UplComponentWriter>) getSessionAttribute(
                        UnifyWebSessionAttributeConstants.UPLCOMPONENT_WRITERS);
        return internalPool.borrowObject(writers);
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
            return (ResponseWriter) getComponent(WebApplicationComponents.APPLICATION_RESPONSEWRITER);
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
