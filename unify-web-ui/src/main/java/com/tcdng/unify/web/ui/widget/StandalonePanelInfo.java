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

package com.tcdng.unify.web.ui.widget;

import java.util.Collection;
import java.util.Map;

public class StandalonePanelInfo {

    private Map<String, WidgetNameInfo> widgetNameInfos;

    private Map<String, PageValidation> pageValidations;

    private Map<String, PageAction> pageActions;

    private boolean sourceInvalidated;

    public StandalonePanelInfo(Map<String, WidgetNameInfo> widgetNameInfos, Map<String, PageValidation> pageValidations,
            Map<String, PageAction> pageActions) {
        this.widgetNameInfos = widgetNameInfos;
        this.pageValidations = pageValidations;
        this.pageActions = pageActions;
        sourceInvalidated = false;
    }

    public Collection<String> getWidgetLongNames() {
        return widgetNameInfos.keySet();
    }

    public Map<String, WidgetNameInfo> getWidgetInfos() {
        return widgetNameInfos;
    }

    public Map<String, PageValidation> getPageValidations() {
        return pageValidations;
    }

    public Map<String, PageAction> getPageActions() {
        return pageActions;
    }

    public void invalidate() {
        sourceInvalidated = true;
    }

    public boolean isSourceInvalidated() {
        return sourceInvalidated;
    }
}