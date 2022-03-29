/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.web.ui.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.ui.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.ui.DocViewBean;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.Panel;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Used for generating document view response.
 * 
 * @author The Code Department
 * @version 1.0
 */
@Component("docviewresponse")
public class DocViewResponse extends AbstractJsonPageControllerResponse {

    public DocViewResponse() {
        super("docViewHdl", false);
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing document view response: path ID = [{0}]", page.getPathId());
        DocViewBean docViewBean = (DocViewBean) page.getPageBean();
        Panel docViewPanel = page.getPanelByShortName(docViewBean.getDocViewPanelName());
        writer.write(",\"remoteTarget\":\"").write(getRequestContextUtil().getRemoteViewer());
        writer.write("\",\"docView\":");
        writer.writeJsonPanel(docViewPanel, false);
    }
}
