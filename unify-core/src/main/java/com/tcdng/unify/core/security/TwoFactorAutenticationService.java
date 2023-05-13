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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * A two-factor authentication service component.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface TwoFactorAutenticationService extends UnifyComponent {

	/**
	 * Send one-time-passcode.
	 * 
	 * @param userName        the user name
	 * @param userEmail       the user email
	 * @throws UnifyException if an error occurs
	 */
	void sendOneTimePasscode(String userName, String userEmail) throws UnifyException;

	/**
	 * Authenticates a user with supplied one-time-passcode.
	 * 
	 * @param userName        the user name to authenticate
	 * @param userEmail       the user email
	 * @param oneTimePasscode the one-time-passcode
	 * @return a true is returned if supplied credentials are successfully
	 *         authenticated
	 * @throws UnifyException if an error occurs
	 */
	boolean authenticate(String userName, String userEmail, String oneTimePasscode) throws UnifyException;
}
