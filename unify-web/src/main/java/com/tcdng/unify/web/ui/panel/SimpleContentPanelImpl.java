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
package com.tcdng.unify.web.ui.panel;

import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.constant.ClosePageMode;
import com.tcdng.unify.web.ui.AbstractContentPanel;
import com.tcdng.unify.web.ui.Page;

/**
 * Simple content panel.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-simplecontentpanel")
public class SimpleContentPanelImpl extends AbstractContentPanel {

    @Override
    public String getBusyIndicatorId() throws UnifyException {
        return getPrefixedId("busy_");
    }

    @Override
    public void clearPages() throws UnifyException {
        
    }

    @Override
    public Page getCurrentPage() {
        return (Page) getContainer();
    }

    @Override
    public void addContent(Page page) throws UnifyException {

    }

    @Override
    public List<String> evaluateRemoveContent(Page page, ClosePageMode closePageMode) throws UnifyException {
        return Collections.emptyList();
    }

    @Override
    public void removeContent(List<String> toRemovePathIdList) throws UnifyException {

    }

}
