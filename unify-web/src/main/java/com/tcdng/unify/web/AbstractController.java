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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.SessionContext;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.logging.EventLogger;
import com.tcdng.unify.core.util.SystemUtils;
import com.tcdng.unify.core.util.ValueStoreUtils;
import com.tcdng.unify.web.constant.Secured;

/**
 * Abstract base controller component.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractController extends AbstractUnifyComponent implements Controller {

    @Configurable
    private EventLogger eventLogger;

    @Configurable
    private ControllerFinder controllerFinder;

    private boolean secured;

    public AbstractController(Secured secured) {
        this.secured = secured.isTrue();
    }

    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

    public void setControllerFinder(ControllerFinder controllerFinder) {
        this.controllerFinder = controllerFinder;
    }

    @Override
    public boolean isSecured() {
        return this.secured;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected ValueStore createValueStore(Object sourceObject) throws UnifyException {
        return ValueStoreUtils.getValueStore(sourceObject, null, 0);
    }

    protected EventLogger getEventLogger() throws UnifyException {
        return eventLogger;
    }

    protected ControllerFinder getControllerFinder() {
        return controllerFinder;
    }
    
    protected void ensureSecureAccess(ControllerPathParts reqPathParts, boolean remoteView) throws UnifyException {
        SessionContext sessionContext = getSessionContext();
        boolean isUserLoggedIn = sessionContext.isUserLoggedIn() || remoteView;
        if (isSecured() && !isUserLoggedIn) {
            String forceLogout = (String) sessionContext
                    .removeAttribute(UnifyWebSessionAttributeConstants.FORCE_LOGOUT);
            if (forceLogout != null) {
                throw new UnifyException(SystemUtils.getSessionAttributeErrorCode(forceLogout),
                        reqPathParts.getControllerPath());
            }

            throw new UnifyException(UnifyWebErrorConstants.LOGIN_REQUIRED, reqPathParts.getControllerPath());
        }
    }
}
