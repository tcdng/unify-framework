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

import com.tcdng.unify.common.constants.StandardFormatType;

/**
 * Parameter token.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ParamToken extends StringToken {

	private final String component;

	private final String param;

	private ParamToken(StringTokenType type, String token, String component, String param) {
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

	public static ParamToken getGeneratorParamToken(String param) {
		return new ParamToken(StringTokenType.GENERATOR_PARAM, "g:" + param, "g", param);
	}

	public static ParamToken getGeneratorParamToken(String component, String param) {
		return new ParamToken(StringTokenType.GENERATOR_PARAM, component + ":" + param, component, param);
	}

	public static ParamToken getFormattedParamToken(StandardFormatType formatType, String param) {
		return new ParamToken(StringTokenType.FORMATTED_PARAM, param + "#" + formatType.code(), formatType.code(),
				param);
	}

	public static ParamToken getParamToken(String token) {
		String[] tokens = token.split(":");
		if (tokens.length == 2) {
			return tokens[0].trim().isEmpty() ? getGeneratorParamToken(tokens[1])
					: getGeneratorParamToken(tokens[0], tokens[1]);
		}

		tokens = token.split("#");
		if (tokens.length == 2) {
			StandardFormatType formatType = StandardFormatType.fromCode(tokens[1]);
			if (formatType != null) {
				return getFormattedParamToken(formatType, tokens[0]);
			}
		}

		return new ParamToken(StringTokenType.PARAM, token, null, token);
	}
}
