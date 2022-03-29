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
package com.tcdng.unify.web.ui.controller;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.annotation.ResultMapping;
import com.tcdng.unify.web.annotation.ResultMappings;
import com.tcdng.unify.web.ui.AbstractPageController;
import com.tcdng.unify.web.ui.widget.control.Table;

/**
 * Unify container information page controller.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("/reserved/info")
@UplBinding("web/reserved/upl/containerinfo.upl")
@ResultMappings({
        @ResultMapping(name = "showcomponentpopup", response = { "!showpopupresponse popup:$s{componentDtlPopup}" }),
        @ResultMapping(name = "refresh", response = { "!refreshpanelresponse panels:$l{content}" }) })
public class ContainerInfoPageController extends AbstractPageController<ContainerInfoPageBean> {

    public ContainerInfoPageController() {
        super(ContainerInfoPageBean.class);
    }

    @Action
    public String refresh() throws UnifyException {
        getContainerInfo();
        return "refresh";
    }

    @Action
    public String prepareViewComponent() throws UnifyException {
        ContainerInfoPageBean containerInfoPageBean = getPageBean();
        containerInfoPageBean.setComponentInfo(containerInfoPageBean.getInfo().getComponentInfoList()
                .get(getPageWidgetByShortName(Table.class, "componentTbl").getViewIndex()));
        return "showcomponentpopup";
    }

    @Action
    public String viewComponentDone() throws UnifyException {
        return hidePopup();
    }

    @Override
    protected void onIndexPage() throws UnifyException {
        getPageBean().setInfo(getContainerInfo());
    }

    @Override
    protected void onOpenPage() throws UnifyException {
        getPageBean().setInfo(getContainerInfo());
    }
}
