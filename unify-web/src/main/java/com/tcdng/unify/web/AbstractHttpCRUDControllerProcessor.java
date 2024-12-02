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
package com.tcdng.unify.web;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.data.ErrorPart;
import com.tcdng.unify.web.data.Response;

/**
 * Convenient abstract base class for HTTP CRUD controller processors.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Singleton(false)
public abstract class AbstractHttpCRUDControllerProcessor extends AbstractUnifyComponent
		implements HttpCRUDControllerProcessor {

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected final Response getErrorResponse(int status, String errorText, String errorMsg) throws UnifyException {
		return new Response(status, DataUtils.asJsonString(new ErrorPart(errorText, errorMsg)));
	}

}
