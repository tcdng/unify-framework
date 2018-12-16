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
package com.tcdng.unify.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.UserPlatform;
import com.tcdng.unify.core.util.ApplicationUtils;

/**
 * Default implementation of request context manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER)
public class RequestContextManagerImpl extends AbstractUnifyComponent implements RequestContextManager {

    private static final ThreadLocal<ThreadRequestContextInfo> requestContextThreadLocal = new ThreadLocal<ThreadRequestContextInfo>() {

        @Override
        protected ThreadRequestContextInfo initialValue() {
            return new ThreadRequestContextInfo(newDefaultContext());
        }
    };

    @Override
    public RequestContext getRequestContext() {
        RequestContext requestContext = requestContextThreadLocal.get().getRequestContext();
        return requestContext != null ? requestContext : requestContextThreadLocal.get().getDefaultRequestContext();
    }

    @Override
    public void loadRequestContext(UserSession userSession, String requestPath) throws UnifyException {
        requestContextThreadLocal.get().setRequestContext(
                new RequestContext(requestPath != null ? requestPath : "", userSession.getSessionContext()));
    }

    @Override
    public void loadRequestContext(RequestContext requestContext) throws UnifyException {
        requestContextThreadLocal.get().setRequestContext(requestContext);
    }

    @Override
    public void unloadRequestContext() {
        requestContextThreadLocal.remove();
    }

    @Override
    public void reset() throws UnifyException {

    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private static RequestContext newDefaultContext() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return new RequestContext(null,
                    new SessionContext(ApplicationUtils.generateSessionContextId(), "http://localhost", "/default",
                            inetAddress.getHostName(), inetAddress.getHostAddress(), null, null, UserPlatform.DEFAULT));
        } catch (UnknownHostException e) {
        }
        return null;
    }

    private static class ThreadRequestContextInfo {

        private RequestContext requestContext;

        private RequestContext defaultRequestContext;

        public ThreadRequestContextInfo(RequestContext defaultRequestContext) {
            this.defaultRequestContext = defaultRequestContext;
        }

        public RequestContext getRequestContext() {
            return requestContext;
        }

        public void setRequestContext(RequestContext requestContext) {
            this.requestContext = requestContext;
        }

        public RequestContext getDefaultRequestContext() {
            return defaultRequestContext;
        }
    }
}
