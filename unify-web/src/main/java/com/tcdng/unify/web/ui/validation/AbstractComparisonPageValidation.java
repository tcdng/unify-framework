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
package com.tcdng.unify.web.ui.validation;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.DataTransfer;
import com.tcdng.unify.web.DataTransferBlock;
import com.tcdng.unify.web.ui.AbstractPageValidation;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.Widget;

/**
 * Abstract regex page validation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractComparisonPageValidation extends AbstractPageValidation {

	private String validationCode;

	private boolean validateSame;

	public AbstractComparisonPageValidation(String validationCode, boolean validateSame) {
		this.validationCode = validationCode;
		this.validateSame = validateSame;
	}

	@Override
	public boolean validate(List<Widget> widgets, DataTransfer dataTransfer) throws UnifyException {
		DataTransferBlock transferBlock1 = dataTransfer.getDataTransferBlock(widgets.get(0).getId());
		DataTransferBlock transferBlock2 = dataTransfer.getDataTransferBlock(widgets.get(1).getId());
		if (transferBlock1 != null && transferBlock2 != null) {
			String value1 = getTransferValue(String.class, transferBlock1);
			String value2 = getTransferValue(String.class, transferBlock2);
			if (value1 != null && value2 != null) {
				if (validateSame != value1.equals(value2)) {
					String caption1 = widgets.get(0).getUplAttribute(String.class, "caption");
					String caption2 = widgets.get(1).getUplAttribute(String.class, "caption");
					String message = null;
					if (validateSame) {
						message = getSessionMessage("validation.notsame", caption1, caption2);
					} else {
						message = getSessionMessage("validation.same", caption1, caption2);
					}
					addValidationFail((Control) widgets.get(0), validationCode, message);
					return false;
				}
			}
		}
		addValidationPass((Control) widgets.get(0), validationCode);
		return true;
	}
}
