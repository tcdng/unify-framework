/*
 * Copyright 2018-2025 The Code Department.
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
 * String composition.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class StringComposition {

	private int length;

	private int letters;

	private int digits;

	private int special;

	private int uppercase;

	private int lowercase;

	public StringComposition(int length, int letters, int digits, int special, int uppercase, int lowercase) {
		this.length = length;
		this.letters = letters;
		this.digits = digits;
		this.special = special;
		this.uppercase = uppercase;
		this.lowercase = lowercase;
	}

	public int getLength() {
		return length;
	}

	public int getLetters() {
		return letters;
	}

	public int getDigits() {
		return digits;
	}

	public int getSpecial() {
		return special;
	}

	public int getUppercase() {
		return uppercase;
	}

	public int getLowercase() {
		return lowercase;
	}

}
