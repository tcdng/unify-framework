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
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.constant.MessageType;
import com.tcdng.unify.web.ui.widget.AbstractFormattedControl;

/**
 * A label control.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-label")
@UplAttributes({
		@UplAttribute(name = "htmlEscape", type = boolean.class, defaultVal = "true"),
		@UplAttribute(name = "layoutCaption", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "bindingOptional", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "inline", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "draggable", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "resolve", type = boolean.class, defaultVal = "false"),
		@UplAttribute(name = "textUppercase", type = boolean.class),
		@UplAttribute(name = "type", type = MessageType.class),
		@UplAttribute(name = "typeBinding", type = String.class) })
public class Label extends AbstractFormattedControl {

	public Label() {
		super.setEditable(false);
	}

	@Override
	public void setEditable(boolean editable) {

	}

	@Override
	public boolean isLayoutCaption() throws UnifyException {
		super.isLayoutCaption();
		return getUplAttribute(boolean.class, "layoutCaption");
	}

	public boolean isBindingOptional() throws UnifyException {
		return getUplAttribute(boolean.class, "bindingOptional");
	}

	public boolean isTextUppercase() throws UnifyException {
		return getUplAttribute(boolean.class, "textUppercase");
	}

	public boolean isInline() throws UnifyException {
		return getUplAttribute(boolean.class, "inline");
	}

	public boolean isResolve() throws UnifyException {
		return getUplAttribute(boolean.class, "resolve");
	}

	public MessageType getType() throws UnifyException {
		MessageType type = null;
		final String typeBinding = getUplAttribute(String.class, "typeBinding");
		if (!StringUtils.isBlank(typeBinding)) {
			type = getValue(MessageType.class, typeBinding);
		}

		return type != null ? type : getUplAttribute(MessageType.class, "type");
	}

	@Override
	public boolean isSupportReadOnly() {
		return false;
	}

	@Override
	public boolean isSupportDisabled() {
		return false;
	}

	public boolean isHtmlEscape() throws UnifyException {
		return getUplAttribute(boolean.class, "htmlEscape");
	}
}
