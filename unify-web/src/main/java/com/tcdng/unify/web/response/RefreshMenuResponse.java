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
package com.tcdng.unify.web.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.UnifyWebSessionAttributeConstants;
import com.tcdng.unify.web.annotation.ResultMapping;
import com.tcdng.unify.web.ui.Document;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.Panel;
import com.tcdng.unify.web.ui.ResponseWriter;

/**
 * Used for generating a refresh document menu response.This response is
 * implicitly added to every result, so there's no need to explicitly add when
 * defining an action result with {@link ResultMapping}.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("refreshmenuresponse")
public class RefreshMenuResponse extends AbstractJsonPageControllerResponse {

    public RefreshMenuResponse() {
        super("refreshMenuHdl", false);
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing refresh menu response: path ID = [{0}]", page.getPathId());
        if (Boolean.TRUE.equals(removeSessionAttribute(UnifyWebSessionAttributeConstants.REFRESH_MENU))) {
            Document document = getRequestContextUtil().getRequestDocument();
            Panel menuPanel = document.getMenuPanel();
            if (menuPanel != null) {
                writer.write(",\"refreshPanels\":[");
                writer.writeJsonPanel(menuPanel, true);
                writer.write("]");
                return;
            }
        }
    }

}
