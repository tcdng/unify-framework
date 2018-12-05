/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.web.ui.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.constant.ExtensionType;
import com.tcdng.unify.web.ui.AbstractFormattedControl;

/**
 * Represents a text field.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-text")
@UplAttributes({ @UplAttribute(name = "size", type = int.class), @UplAttribute(name = "minLen", type = int.class),
		@UplAttribute(name = "maxLen", type = int.class), @UplAttribute(name = "case", type = String.class),
		@UplAttribute(name = "extStyleClass", type = String.class, defaultValue = "tread"),
		@UplAttribute(name = "extReadOnly", type = boolean.class, defaultValue = "true") })
public class TextField extends AbstractFormattedControl {

	public String getCase() throws UnifyException {
		return this.getUplAttribute(String.class, "case");
	}

	public String getFacadeStringValue() throws UnifyException {
		return this.getStringValue();
	}

	public String getExtStyleClass() throws UnifyException {
		return this.getUplAttribute(String.class, "extStyleClass");
	}

	public boolean getExtReadOnly() throws UnifyException {
		return this.getUplAttribute(boolean.class, "extReadOnly");
	}

	public ExtensionType getExtensionType() {
		return ExtensionType.NONE;
	}

}
