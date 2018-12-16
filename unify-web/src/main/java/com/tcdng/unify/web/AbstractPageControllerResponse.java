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
package com.tcdng.unify.web;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.format.FormatHelper;
import com.tcdng.unify.core.upl.AbstractUplComponent;
import com.tcdng.unify.web.ui.PageManager;
import com.tcdng.unify.web.ui.ResponseWriter;

/**
 * Convenient abstract class that provides appenders for generating a response
 * to a web request.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractPageControllerResponse extends AbstractUplComponent implements PageControllerResponse {

    @Configurable(WebApplicationComponents.APPLICATION_REQUESTCONTEXTUTIL)
    private RequestContextUtil requestContextUtil;

    @Configurable(WebApplicationComponents.APPLICATION_PAGEMANAGER)
    private PageManager pageManager;

    @Configurable(ApplicationComponents.APPLICATION_FORMATHELPER)
    private FormatHelper formatHelper;

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected PageManager getPageManager() throws UnifyException {
        return pageManager;
    }

    protected RequestContextUtil getRequestContextUtil() throws UnifyException {
        return requestContextUtil;
    }

    protected FormatHelper getFormatHelper() throws UnifyException {
        return formatHelper;
    }

    protected void appendRefreshAttributesJson(ResponseWriter writer, boolean clearShortcuts) throws UnifyException {
        writer.write("\"clearShortcuts\":").write(clearShortcuts);
        writer.write(",\"pageNameAliases\":");
        writer.writeJsonPageNameAliasesArray();
    }
}
