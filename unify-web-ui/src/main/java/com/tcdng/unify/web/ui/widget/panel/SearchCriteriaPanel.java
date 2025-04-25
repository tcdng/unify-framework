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
package com.tcdng.unify.web.ui.widget.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.web.ui.widget.AbstractPanel;

/**
 * Represents a search criteria panel.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-searchcriteriapanel")
@UplBinding("web/panels/upl/searchcriteriapanel.upl")
public class SearchCriteriaPanel extends AbstractPanel {

    public void showActionButtons() throws UnifyException {
        setVisible("buttonPanel", true);
    }

    public void hideActionButtons() throws UnifyException {
        setVisible("buttonPanel", false);
    }
}
