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
import com.tcdng.unify.core.format.Pattern;
import com.tcdng.unify.web.constant.ExtensionType;

/**
 * Abstract base class for popup time fields.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractTimeField extends AbstractPopupTextField {

	private Pattern[] pattern;

	@Override
	public void onPageInitialize() throws UnifyException {
		super.onPageInitialize();
		pattern = getFormatter().getFormatHelper().splitDatePattern(getFormatter().getPattern());
	}

	@Override
	public ExtensionType getExtensionType() {
		return ExtensionType.EXTENDED;
	}

	@Override
	public boolean isPopupOnEditableOnly() {
		return true;
	}

	public Pattern[] getPattern() throws UnifyException {
		return pattern;
	}
}
