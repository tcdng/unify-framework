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
package com.tcdng.unify.core.net;

/**
 * Test text response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TestTextResponse implements NetworkMessage {

	private static final long serialVersionUID = 3691731772102685479L;

	private String fullname;

	public TestTextResponse(String fullname) {
		this.fullname = fullname;
	}

	public String getFullname() {
		return fullname;
	}

}
