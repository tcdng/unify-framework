/*
 * Copyright (c) 2018-2025 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.format.FormatHelper;
import com.tcdng.unify.core.upl.AbstractUplComponent;
import com.tcdng.unify.web.ui.widget.PageManager;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Convenient abstract class that provides appenders for generating a response
 * to a web request.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractPageControllerResponse extends AbstractUplComponent implements PageControllerResponse {

    @Configurable
    private PageRequestContextUtil requestContextUtil;

    @Configurable
    private PageManager pageManager;

    @Configurable
    private FormatHelper formatHelper;
    
    @Override
	public boolean isDocumentPathResponse() throws UnifyException {
		return false;
	}

	@Override
	public String getDocumentPath() throws UnifyException {
		return null;
	}

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected PageManager getPageManager() throws UnifyException {
        return pageManager;
    }

    protected PageRequestContextUtil getRequestContextUtil() throws UnifyException {
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
