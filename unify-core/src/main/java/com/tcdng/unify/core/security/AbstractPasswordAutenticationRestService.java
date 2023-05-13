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
package com.tcdng.unify.core.security;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient abstract base class for password authentication REST service.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractPasswordAutenticationRestService extends AbstractPasswordAutenticationService {

	@Override
	public boolean authenticate(String userName, String userEmail, String password) throws UnifyException {
		PasswordAuthResponse resp = IOUtils.postObjectToEndpointUsingJson(PasswordAuthResponse.class, getAuthEndpoint(),
				new PasswordAuthRequest(userName, userEmail, password));
		return resp.isSuccess();
	}

	protected abstract String getAuthEndpoint() throws UnifyException;
}
