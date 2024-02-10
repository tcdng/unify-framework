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
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.web.constant.ExtensionType;

/**
 * Abstract base class for number fields.
 * 
 * @author The Code Department
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "precision", type = int.class),
		@UplAttribute(name = "acceptNegative", type = boolean.class),
		@UplAttribute(name = "useGrouping", type = boolean.class),
		@UplAttribute(name = "formatter", type = Formatter.class),
		@UplAttribute(name = "mimic", type = boolean.class),
	    @UplAttribute(name = "clientFormat", type = boolean.class, defaultVal = "true"),
		@UplAttribute(name = "strictFormat", type = boolean.class) })
public abstract class AbstractNumberField extends TextField {

	public ExtensionType getExtensionType() throws UnifyException {
		return isHiddenMimic() ? ExtensionType.FACADE_HIDDEN_EDIT : super.getExtensionType();
	}

	@Override
	public boolean isUseFacade() throws UnifyException {
		return isHiddenMimic() ? true : super.isUseFacade();
	}

	@Override
	public boolean isHiddenMimic() throws UnifyException {
		return getUplAttribute(boolean.class, "mimic");
	}

	public int getScale() throws UnifyException {
		return isUplAttribute("scale") ?  getUplAttribute(int.class, "scale") : 0;
	}

	public int getPrecision() throws UnifyException {
		return getUplAttribute(int.class, "precision");
	}

	public boolean isAcceptNegative() throws UnifyException {
		return getUplAttribute(boolean.class, "acceptNegative");
	}

	public boolean isUseGrouping() throws UnifyException {
		return getUplAttribute(boolean.class, "useGrouping");
	}

	public boolean isStrictFormat() throws UnifyException {
		return getUplAttribute(boolean.class, "strictFormat");
	}

}
