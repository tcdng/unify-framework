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
package com.tcdng.unify.core.format;

/**
 * A pattern data object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Pattern {

	private String pattern;

	private String target;

	private boolean filler;

	private boolean quoted;

	public Pattern(String pattern, boolean filler) {
		this(pattern, filler, false);
	}

	public Pattern(String pattern, boolean filler, boolean quoted) {
		this.pattern = pattern;
		this.filler = filler;
		this.quoted = quoted;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.filler) {
			sb.append('f');
			if (this.quoted) {
				sb.append('q');
			}
		} else {
			sb.append('p');
		}
		sb.append('[').append(pattern).append(']');
		return sb.toString();
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getPattern() {
		return pattern;
	}

	public boolean isFiller() {
		return filler;
	}

	public boolean isQuoted() {
		return quoted;
	}
}
