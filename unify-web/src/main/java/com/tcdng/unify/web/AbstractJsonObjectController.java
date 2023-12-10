/*
 * Copyright 2018-2023 The Code Department.
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
import com.tcdng.unify.core.annotation.Configurable;

/**
 * Abstract JSON object controller.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractJsonObjectController extends AbstractPlainJsonController {

    @Configurable
    private ControllerUtil controllerUtil;

	public final void setControllerUtil(ControllerUtil controllerUtil) {
		this.controllerUtil = controllerUtil;
	}

	@Override
	protected final String doExecute(String actionName, String jsonRequest) throws UnifyException {
		try {
			PlainControllerInfo pcInfo = controllerUtil.getPlainControllerInfo(getName());
			Action action = pcInfo.getAction(actionName);			
			Object req = getObjectFromRequestJson(action.getParamType(), jsonRequest);
			Object resp = action.getMethod().invoke(this, req);
			return getResponseJsonFromObject(resp);
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throwOperationErrorException(e);
		}
		
		return null;
	}

}
