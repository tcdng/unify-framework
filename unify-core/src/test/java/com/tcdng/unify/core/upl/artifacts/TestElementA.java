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
package com.tcdng.unify.core.upl.artifacts;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.upl.AbstractUplComponent;
import com.tcdng.unify.core.upl.UplElementReferences;

/**
 * Simple test UPL component.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("test-uplelementa")
@UplAttributes({ @UplAttribute(name = "name", type = String.class),
        @UplAttribute(name = "description", type = String.class, defaultValue = "Application User"),
        @UplAttribute(name = "friendList", type = String[].class),
        @UplAttribute(name = "rateList", type = Double[].class),
        @UplAttribute(name = "age", type = int.class, defaultValue = "20"),
        @UplAttribute(name = "action", type = UplElementReferences.class),
        @UplAttribute(name = "components", type = UplElementReferences.class),
        @UplAttribute(name = "componentRef", type = String.class) })
public class TestElementA extends AbstractUplComponent {

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
