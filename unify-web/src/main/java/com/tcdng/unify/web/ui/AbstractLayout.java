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
package com.tcdng.unify.web.ui;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.upl.AbstractUplComponent;

/**
 * Serves as a base class container layout manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "styleClass", type = String.class, defaultValue = "$e{}"),
		@UplAttribute(name = "style", type = String.class),
		@UplAttribute(name = "showCaption", type = boolean.class, defaultValue = "false"),
		@UplAttribute(name = "captionStyle", type = String.class),
		@UplAttribute(name = "captionSuffix", type = String.class),
		@UplAttribute(name = "inlineCaption", type = boolean.class) })
public abstract class AbstractLayout extends AbstractUplComponent implements Layout {

	@Override
	public boolean isInlineCaption() throws UnifyException {
		return getUplAttribute(boolean.class, "inlineCaption");
	}

	@Override
	public boolean isShowCaption() throws UnifyException {
		return getUplAttribute(boolean.class, "showCaption");
	}

	@Override
	public String getCaptionStyle() throws UnifyException {
		return getUplAttribute(String.class, "captionStyle");
	}

	@Override
	public String getCaptionSuffix() throws UnifyException {
		return getUplAttribute(String.class, "captionSuffix");
	}

	@Override
	public String getStyleClass() throws UnifyException {
		return getUplAttribute(String.class, "styleClass");
	}

	@Override
	public String getStyle() throws UnifyException {
		return getUplAttribute(String.class, "style");
	}

}
