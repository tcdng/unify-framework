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
import com.tcdng.unify.web.ui.AbstractTargetControl;

/**
 * An image control.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-image")
@UplAttributes({ @UplAttribute(name = "src", type = String.class),
		@UplAttribute(name = "srcBinding", type = String.class), @UplAttribute(name = "scope", type = String.class),
		@UplAttribute(name = "clearOnRead", type = boolean.class, defaultValue = "true"),
		@UplAttribute(name = "alwaysFetch", type = boolean.class) })
public class Image extends AbstractTargetControl {

	public String getSrc() throws UnifyException {
		return getUplAttribute(String.class, "src");
	}

	public String getSrcBinding() throws UnifyException {
		return getUplAttribute(String.class, "srcBinding");
	}

	public String getScope() throws UnifyException {
		return getUplAttribute(String.class, "scope");
	}

	public boolean isClearOnRead() throws UnifyException {
		return getUplAttribute(boolean.class, "clearOnRead");
	}

	public boolean isAlwaysFetch() throws UnifyException {
		return getUplAttribute(boolean.class, "alwaysFetch");
	}
}
