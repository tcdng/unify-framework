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

import com.tcdng.unify.core.format.StandardFormatType;

/**
 * Parameter token.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ParamToken extends StringToken {

	private final String component;

	private final String param;

	private ParamToken(Type type, String token, String component, String param) {
		super(type, token);
		this.component = component;
		this.param = param;
	}

	public String getComponent() {
		return component;
	}

	public StandardFormatType getFormatType() {
		return StandardFormatType.fromCode(component);
	}

	public String getParam() {
		return param;
	}

	public ParamToken getGeneratorParamToken(String component, String param) {
		return new ParamToken(Type.GENERATOR_PARAM, component + ":" + param, component, param);
	}

	public ParamToken getFormattedParamToken(StandardFormatType formatType, String param) {
		return new ParamToken(Type.FORMATTED_PARAM, param + "#" + formatType.code(), formatType.code(), param);
	}

	public static ParamToken getParamToken(String token) {
		String[] tokens = token.split(":");
		if (tokens.length == 2) {
			return new ParamToken(Type.GENERATOR_PARAM, token, tokens[0], tokens[1]);
		}

		tokens = token.split("#");
		if (tokens.length == 2) {
			return new ParamToken(Type.FORMATTED_PARAM, token, tokens[1], tokens[0]);
		}

		return new ParamToken(Type.PARAM, token, null, token);
	}
}
