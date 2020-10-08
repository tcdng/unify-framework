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

package com.tcdng.unify.web.ui;

import com.tcdng.unify.web.constant.ReadOnly;
import com.tcdng.unify.web.constant.ResetOnWrite;
import com.tcdng.unify.web.constant.Secured;

/**
 * Convenient abstract base class for void page bean page controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractVoidBeanPageController extends AbstractPageController<VoidPageBean> {

    public AbstractVoidBeanPageController() {
        super(VoidPageBean.class);
    }

    public AbstractVoidBeanPageController(Secured secured) {
        super(VoidPageBean.class, secured, ReadOnly.TRUE, ResetOnWrite.FALSE);
    }

}
