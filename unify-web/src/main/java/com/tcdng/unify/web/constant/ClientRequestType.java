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

package com.tcdng.unify.web.constant;

/**
 * Client request type enumeration.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum ClientRequestType {

	GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

	public boolean isGet() {
		return GET.equals(this);
	}

	public boolean isHead() {
		return HEAD.equals(this);
	}

	public boolean isPost() {
		return POST.equals(this);
	}

	public boolean isPut() {
		return PUT.equals(this);
	}

	public boolean isPatch() {
		return PATCH.equals(this);
	}

	public boolean isDelete() {
		return DELETE.equals(this);
	}

	public boolean isOptions() {
		return OPTIONS.equals(this);
	}

	public boolean isTrace() {
		return TRACE.equals(this);
	}

	public boolean isCRUD() {
		return GET.equals(this) || POST.equals(this) || PUT.equals(this) || PATCH.equals(this) || DELETE.equals(this);
	}
}
