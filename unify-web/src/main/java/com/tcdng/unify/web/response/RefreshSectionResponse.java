/*
 * Copyright 2018-2019 The Code Department.
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
import com.tcdng.unify.web.PageController;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.data.RefreshSection;

/**
 * Used for generating a refresh section response. A section is a division
 * rendered with a div tag. Refresh section data is gotten from request context.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("refreshsectionresponse")
public class RefreshSectionResponse extends AbstractJsonPageControllerResponse {

    public RefreshSectionResponse() {
        super("refreshSectionHdl");
    }

    @Override
    protected void doGenerate(ResponseWriter writer, PageController pageController) throws UnifyException {
        RefreshSection refreshSection =
                (RefreshSection) this.getRequestAttribute(UnifyWebRequestAttributeConstants.REFRESH_SECTION);
        if (refreshSection != null) {
            Widget widget = refreshSection.getWidget();
            String sectionPageName = refreshSection.getSectionPageName();
            logDebug("Preparing refresh section response: controller = [{0}], component = [{1}], section= [{2}]",
                    pageController.getName(), widget.getLongName(), sectionPageName);
            writer.write(",\"section\":").writeJsonSection(widget, sectionPageName);
        } else {
            logDebug("Preparing refresh section response: Can not get section information from request context");
        }
    }

}
