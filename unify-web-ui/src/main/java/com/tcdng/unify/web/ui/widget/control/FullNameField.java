/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.constant.TextCase;

/**
 * An input text field that allows only full name.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-fullname")
@UplAttributes({ @UplAttribute(name = "case", type = TextCase.class, defaultVal = "CAPITAL"),
        @UplAttribute(name = "special", type = boolean.class, defaultVal = "false") })
public class FullNameField extends TextField {

    public boolean isSpecial() throws UnifyException {
        return getUplAttribute(boolean.class, "special");
    }

}
