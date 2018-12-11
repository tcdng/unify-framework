/*
 * Copyright 2018 The Code Department
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

import java.io.Serializable;

/**
 * Test address bean.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Address extends AbstractDocument implements Serializable {

	private static final long serialVersionUID = -8032533711912478984L;

	private String line1;

	private String line2;

	public Address(String line1, String line2) {
		this.line1 = line1;
		this.line2 = line2;
	}

	public Address() {

	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public Object getId() {
		return null;
	}

	@Override
	public Object getOwnerId() {
		return null;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}
}
