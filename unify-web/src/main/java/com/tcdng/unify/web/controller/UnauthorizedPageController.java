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
package com.tcdng.unify.web.controller;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.web.AbstractPageController;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.annotation.ResultMapping;
import com.tcdng.unify.web.annotation.ResultMappings;
import com.tcdng.unify.web.constant.SystemInfoConstants;

/**
 * Unauthorized back door page controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Singleton
@Component(SystemInfoConstants.UNAUTHORIZED_CONTROLLER_NAME)
@UplBinding("web/reserved/upl/unauthorized.upl")
@ResultMappings({ @ResultMapping(
        name = SystemInfoConstants.FORWARD_TO_APPLICATION_MAPPING,
        response = { "!hidepopupresponse systemInfo:true", "!forwardresponse path:$x{application.web.home}" }) })
public class UnauthorizedPageController extends AbstractPageController<UnauthorizedPageBean> {

    public UnauthorizedPageController() {
        super(UnauthorizedPageBean.class);
    }

    @Action
    public String closeBackdoor() throws UnifyException {
        return SystemInfoConstants.FORWARD_TO_APPLICATION_MAPPING;
    }
}
