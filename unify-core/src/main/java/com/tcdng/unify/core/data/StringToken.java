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

package com.tcdng.unify.core.data;

/**
 * String token.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class StringToken {

	public enum Type {
		TEXT,
		NEWLINE,
		PARAM,
		FORMATTED_PARAM,
		GENERATOR_PARAM
	}
    
    private final Type type;
	
    private final String token;

    protected StringToken(Type type, String token) {
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

    public Type getType() {
		return type;
	}

	public boolean isParam() {
        return Type.PARAM.equals(type) ||Type.FORMATTED_PARAM.equals(type) ||Type.GENERATOR_PARAM.equals(type);
    }

    public boolean isFormattedParam() {
        return Type.FORMATTED_PARAM.equals(type);
    }

    public boolean isGeneratorParam() {
        return Type.GENERATOR_PARAM.equals(type);
    }
}