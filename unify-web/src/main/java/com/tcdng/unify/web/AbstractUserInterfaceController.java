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
package com.tcdng.unify.web;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Convenient base class for user interface controller.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Singleton(false)
public abstract class AbstractUserInterfaceController extends AbstractController implements UserInterfaceController {

	private boolean readOnly;

	public AbstractUserInterfaceController(boolean secured, boolean readOnly) {
		super(secured);
		this.readOnly = readOnly;
	}

	@Override
	public boolean isReadOnly() {
		return this.readOnly;
	}

	@Override
	public boolean isUserInterface() {
		return true;
	}

	@Override
	public void reset() {

	}

	@Override
	public void populate(DataTransferBlock transferBlock) throws UnifyException {
		if (!this.readOnly) {
			DataUtils.setNestedBeanProperty(this, transferBlock.getLongProperty(), transferBlock.getValue(), null);
		}
	}

}
