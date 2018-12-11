/*
 * Copyright 2018 The Code Department
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
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;

/**
 * Abstract base class for popup text fields.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "buttonImgSrc", type = String.class, defaultValue = "$t{images/droparrow.png}"),
		@UplAttribute(name = "timeout", type = long.class, defaultValue = "-1"),
		@UplAttribute(name = "clearable", type = boolean.class),
		@UplAttribute(name = "popupAlways", type = boolean.class, defaultValue = "false") })
public abstract class AbstractPopupTextField extends TextField {

	@Override
	public String getBorderId() throws UnifyException {
		return getPrefixedId("brd_");
	}

	@Override
	public String getStyleClass() throws UnifyException {
		return super.getStyleClass() + " ui-text-popup";
	}

	public String getPopupButtonId() throws UnifyException {
		return getPrefixedId("popb_");
	}

	public String getPopupId() throws UnifyException {
		return getPrefixedId("pop_");
	}

	public String getButtonImageSrc() throws UnifyException {
		return getUplAttribute(String.class, "buttonImgSrc");
	}

	public long getDisplayTimeOut() throws UnifyException {
		return getUplAttribute(long.class, "timeout");
	}

	public boolean isClearable() throws UnifyException {
		return getUplAttribute(boolean.class, "clearable");
	}

	public boolean isPopupAlways() throws UnifyException {
		return getUplAttribute(boolean.class, "popupAlways");
	}

	public abstract boolean isPopupOnEditableOnly();
}
