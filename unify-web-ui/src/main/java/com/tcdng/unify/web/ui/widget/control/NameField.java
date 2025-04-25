/*
 * Copyright 2018-2025 The Code Department.
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

/**
 * An input text field that allows only alphanumeric characters with optional
 * underscore, dollar and period.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-name")
@UplAttributes({
		@UplAttribute(name = "underscore", type = boolean.class, defaultVal = "false"),
        @UplAttribute(name = "dollar", type = boolean.class, defaultVal = "false"),
        @UplAttribute(name = "period", type = boolean.class, defaultVal = "false"),
        @UplAttribute(name = "dash", type = boolean.class, defaultVal = "false"),
        @UplAttribute(name = "slash", type = boolean.class, defaultVal = "false"),
        @UplAttribute(name = "special", type = boolean.class, defaultVal = "false")})
public class NameField extends TextField {

    public boolean isAcceptUnderscore() throws UnifyException {
        return getUplAttribute(boolean.class, "underscore");
    }

    public boolean isAcceptDollar() throws UnifyException {
        return getUplAttribute(boolean.class, "dollar");
    }

    public boolean isAcceptPeriod() throws UnifyException {
        return getUplAttribute(boolean.class, "period");
    }

    public boolean isAcceptDash() throws UnifyException {
        return getUplAttribute(boolean.class, "dash");
    }

    public boolean isAcceptSlash() throws UnifyException {
        return getUplAttribute(boolean.class, "slash");
    }

    public boolean isAcceptSpecial() throws UnifyException {
        return getUplAttribute(boolean.class, "special");
    }
}
