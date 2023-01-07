/*
 * Copyright 2018-2020 The Code Department.
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

package com.tcdng.unify.common.util;

/**
 * String token.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class StringToken {
    
    private final StringTokenType type;
	
    private final String token;

    protected StringToken(StringTokenType type, String token) {
        this.type = type;
        this.token = token;
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringToken && token.equals(((StringToken) obj).token);
    }

    public String getToken() {
        return token;
    }

    public StringTokenType getType() {
		return type;
	}

	public boolean isText() {
        return type.isText();
    }

	public boolean isNewline() {
        return type.isNewline();
    }

	public boolean isParam() {
        return type.isParam();
    }

    public boolean isFormattedParam() {
        return type.isFormattedParam();
    }

    public boolean isGeneratorParam() {
        return type.isGeneratorParam();
    }

	@Override
	public String toString() {
		return "{type=" + type + ", token=" + token + "}";
	}
}