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
import com.tcdng.unify.web.ui.AbstractControl;

/**
 * A text area widget.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-textarea")
@UplAttributes({ @UplAttribute(name = "columns", type = int.class), @UplAttribute(name = "rows", type = int.class),
		@UplAttribute(name = "minLen", type = int.class), @UplAttribute(name = "maxLen", type = int.class),
		@UplAttribute(name = "wordWrap", type = boolean.class, defaultValue = "true"),
		@UplAttribute(name = "scrollToEnd", type = boolean.class) })
public class TextArea extends AbstractControl {

	public int getColumns() throws UnifyException {
		return this.getUplAttribute(int.class, "columns");
	}

	public int getRows() throws UnifyException {
		return this.getUplAttribute(int.class, "rows");
	}

	public int getMinLen() throws UnifyException {
		return this.getUplAttribute(int.class, "minLen");
	}

	public int getMaxLen() throws UnifyException {
		return this.getUplAttribute(int.class, "maxLen");
	}

	public boolean isWordWrap() throws UnifyException {
		return this.getUplAttribute(boolean.class, "wordWrap");
	}

	public boolean isScrollToEnd() throws UnifyException {
		return this.getUplAttribute(boolean.class, "scrollToEnd");
	}
}
