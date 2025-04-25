/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.core.data;

/**
 * Alternate privilege.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class AlternativePrivilege {

	private String categoryCode;

	private String privilege;

	private String altPrivilege;

	public AlternativePrivilege(String categoryCode, String privilege, String altPrivilege) {
		this.categoryCode = categoryCode;
		this.privilege = privilege;
		this.altPrivilege = altPrivilege;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public String getPrivilege() {
		return privilege;
	}

	public String getAltPrivilege() {
		return altPrivilege;
	}

}
