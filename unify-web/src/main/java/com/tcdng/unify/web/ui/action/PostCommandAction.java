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
package com.tcdng.unify.web.ui.action;

import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.web.ui.AbstractPageAction;

/**
 * Post command action.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-postcommand")
@UplAttributes({ @UplAttribute(name = "command", type = String.class, defaultValue = "switchState"),
        @UplAttribute(name = "target", type = String.class),
        @UplAttribute(name = "validations", type = UplElementReferences.class),
        @UplAttribute(name = "refresh", type = UplElementReferences.class) })
public class PostCommandAction extends AbstractPageAction {

    public PostCommandAction() {
        super("postcommand");
    }
}
