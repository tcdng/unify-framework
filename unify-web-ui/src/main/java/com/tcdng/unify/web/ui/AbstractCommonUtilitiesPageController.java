/*
 * Copyright 2018-2023 The Code Department.
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

import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.web.annotation.ResultMapping;
import com.tcdng.unify.web.annotation.ResultMappings;
import com.tcdng.unify.web.constant.ReadOnly;
import com.tcdng.unify.web.constant.ResetOnWrite;
import com.tcdng.unify.web.constant.Secured;

/**
 * Base class for common utilities page controller.
 * 
 * @author The Code Department
 * @since 1.0
 */
@UplBinding("web/common/upl/commonutilities.upl")
@ResultMappings({
        @ResultMapping(name = "showapplicationmessage", response = { "!showpopupresponse popup:$s{messageBoxPopup}" }),
        @ResultMapping(
                name = "showapplicationtaskmonitor",
                response = { "!showpopupresponse popup:$s{taskMonitorInfoPopup}" }) })
public abstract class AbstractCommonUtilitiesPageController<T extends AbstractPageBean>
        extends AbstractPageController<T> {

    public AbstractCommonUtilitiesPageController(Class<T> pageBeanClass) {
        super(pageBeanClass, Secured.TRUE, ReadOnly.FALSE, ResetOnWrite.FALSE);
    }

}
