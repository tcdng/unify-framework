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
 * Represents a drop-down list with options for selecting symbols.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-symbolselect")
	@UplAttributes({ @UplAttribute(name = "htmlEscape", type = boolean.class, defaultVal = "false"),
	@UplAttribute(name = "list", type = String.class, defaultVal = "buttonsymbollist") })
public class SymbolSelect extends SingleSelect {

	public String getExtStyleClass() throws UnifyException {
		return isHtmlEscape() ? getUplAttribute(String.class, "extStyleClass")
				: getUplAttribute(String.class, "extStyleClass") + " g_fsm";
	}

}
